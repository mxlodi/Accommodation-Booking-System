# Java Methodology — Accommodation Booking System

## 1. Project Logic Flow

```
┌──────────────────────────────────────────────────────────────────┐
│                       MAIN.java (Entry Point)                    │
│                                                                  │
│  [1] System Initialization                                       │
│      ├── Create Users (Teddy, Alice)                             │
│      ├── Create Accommodations (Hotel, Apartment, GuestHouse)    │
│      └── Register all into BookingRepository                     │
│                                                                  │
│  [2] Exception Handling Demo                                     │
│      ├── Transaction #1: Valid booking → success                 │
│      ├── Transaction #2: Invalid dates (checkOut < checkIn)      │
│      │                          → InvalidBookingException caught │
│      └── Transaction #3: Null booking → InvalidBookingException  │
│                                                                  │
│  [3] Anonymous Inner Class vs Lambda Demo                        │
│      ├── anonymousDisplay: new Displayable() { ... }             │
│      └── lambdaDisplay: () -> System.out.println(...)            │
│                                                                  │
│  [4] Full System Overview                                        │
│      └── repo.displayAllData(() -> ...)                          │
│                                                                  │
│  [5] Additional Demonstrations                                   │
│      ├── Polymorphism: iterate Accommodation list,               │
│      │   check instanceof Hotel, cast to Hotel                   │
│      ├── Method Overloading: calculatePrice(3) vs (3,66)        │
│      └── Booking Summary via polymorphism                        │
│                                                                  │
│  [6] System Closure                                              │
└──────────────────────────────────────────────────────────────────┘
```

### Data Flow

```
User ──> Booking ──> Accommodation (abstract)
                        ├── Hotel
                        ├── Apartment
                        └── GuestHouse
Booking ──> Payment
     ↕
BookingRepository (in-memory storage)
  ├── Map<Integer, Booking> allBookings
  ├── List<User> users
  ├── List<Accommodation> accommodations
  └── List<Payment> payments
```

- `Main.java` drives all demonstrations sequentially.
- `BookingRepository` acts as the central service layer (repository pattern) — all CRUD, validation, search, and statistics live here.
- `Accommodation` is the abstract root of the accommodation hierarchy. `Hotel`, `Apartment`, and `GuestHouse` extend it.
- `Booking` links a `User` to an `Accommodation` with date range and `BookingStatus` (enum).
- `Payment` wraps a `Booking` with amount, method, and completion status.

---

## 2. Java Concepts — Theory & Application

### 2.1 Polymorphism

**Theory:**
Polymorphism means "many forms." In Java, it allows a parent reference variable to hold an object of any subclass, and the correct overridden method is called at runtime based on the actual object type (dynamic method dispatch). Two types:
- **Compile-time (static):** Method overloading — resolved at compile time.
- **Runtime (dynamic):** Method overriding — resolved at runtime via the JVM.

**Applied in this project:**

**(A) Dynamic Polymorphism (Method Overriding) — `Accommodation` reference, subclass objects**

File: `BookingRepository.java:298-303`
```java
for (Accommodation a : accommodations) {
    a.display();   // Calls Hotel.display(), Apartment.display(), or GuestHouse.display()
}
```
The list holds `Accommodation` references, but the actual runtime type determines which `display()` is invoked. Each subclass overrides `display()`; `Hotel` adds stars/pool/breakfast, `Apartment` adds floor/elevator, `GuestHouse` adds breakfast/elevator. The same principle applies at `Main.java:99-108` where `a.calculatePrice(...)` dispatches to the inherited method.

**(B) `instanceof` + Casting Pattern**

File: `Main.java:99-108`
```java
if (acc instanceof Hotel) {
    Hotel h = (Hotel) acc;
    if (h.getStars() >= 4 && h.hasPool()) { ... }
}
```
Polymorphism lets us iterate a homogeneous `List<Accommodation>` and then use `instanceof` to access subclass-specific fields (`getStars()`, `hasPool()`).

**(C) `Displayable` Interface Polymorphism**

File: `BookingRepository.java:276-280`
```java
public void displayAllData(Displayable reportStyle) {
    reportStyle.display();  // polymorphic — could be anonymous class or lambda
    ...
}
```
The method accepts any `Displayable` implementation. `Main.java:87` passes a lambda, `Main.java:67-73` passes an anonymous inner class — both satisfy the `Displayable` contract polymorphically.

**(D) Method Overloading (Compile-time Polymorphism) — `calculatePrice`**

File: `Accommodation.java:78-100`
```java
public double calculatePrice(int nights) { ... }                       // overload 1
public double calculatePrice(int nights, double discount) { ... }      // overload 2
public double calculatePrice(LocalDateTime in, LocalDateTime out) { ... } // overload 3
```
Three versions of `calculatePrice` with different parameter lists. The compiler resolves which to call at compile time.

**Overloading in `BookingRepository.addBooking`:**

File: `BookingRepository.java:138-166`
```java
public void addBooking(Booking booking) throws InvalidBookingException { ... } // overload 1
public void addBooking(User user, Accommodation acc, String in, String out) ... // overload 2
```
First accepts a `Booking` object, second constructs one internally from raw parameters.

**Overloading in `Payment.processPayment`:**

File: `Payment.java:85-107`
```java
public void processPayment() { ... }                                      // overload 1
public void processPayment(PaymentMethod method) { ... }                  // overload 2
public void processPayment(PaymentMethod method, double discount) { ... } // overload 3
```

---

### 2.2 Abstract Classes & Methods

**Theory:**
An `abstract` class cannot be instantiated directly. It serves as a base class that defines a common template. Abstract methods have no body — subclasses **must** override them (enforced by the compiler). This ensures a consistent API across subclasses while allowing each to provide its own implementation.

**Applied in this project:**

**(A) `Accommodation` — the abstract class**

File: `Accommodation.java:8`
```java
public abstract class Accommodation implements Displayable, Bookable {
```

- **Cannot be instantiated:** You cannot write `new Accommodation(...)`. Only `Hotel`, `Apartment`, `GuestHouse` can be instantiated.
- **Common state:** Fields `accId`, `name`, `pricePerNight`, `capacity` are shared. Their setters include validation logic (e.g., `setPricePerNight` defaults to 50.0 if negative).
- **Common behavior:** `canAccommodate()`, `calculatePrice()` (2 overloads), `isAvailable()` — all concrete methods inherited by subclasses.
- **Template Method:** `display()` in `Accommodation` prints base fields, subclasses call `super.display()` then add their own fields.

**(B) Abstract method `getType()`**

File: `Accommodation.java:62`
```java
public abstract String getType();
```

Each subclass **must** implement this. The compiler enforces it:

File: `Hotel.java:21`
```java
@Override
public String getType() { return "HOTEL"; }
```

File: `Apartment.java:16`
```java
@Override
public String getType() { return "APARTMENT"; }
```

File: `GuestHouse.java:12`
```java
@Override
public String getType() { return "GUEST_HOUSE"; }
```

This is used in `Accommodation.display()` (line 109):
```java
System.out.println("[" + getType() + "] " + name);
```
Even though `display()` is defined in the abstract class, the polymorphic call to `getType()` resolves to the correct subclass implementation.

---

### 2.3 Exception Handling

**Theory:**
Exceptions in Java are objects that represent abnormal conditions. The `try-catch-finally` mechanism separates error-handling code from normal logic:
- `try` — wraps code that may throw an exception.
- `catch` — handles the exception (program can recover).
- `finally` — always executes (cleanup: close files, release resources).

Checked exceptions must be declared (`throws`) or caught. Custom exceptions extend `Exception` (checked) or `RuntimeException` (unchecked).

**Applied in this project:**

**(A) Custom Exception — `InvalidBookingException`**

File: `BookingRepository.java:19-22`
```java
public static class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}
```
This is a **static nested class** inside `BookingRepository`. It extends `Exception` (checked), so callers must either catch it or declare `throws`.

**(B) Throwing — validation in `addBooking`**

File: `BookingRepository.java:139-148`
```java
public void addBooking(Booking booking) throws InvalidBookingException {
    if (booking == null || booking.getUser() == null || booking.getAccommodation() == null) {
        throw new InvalidBookingException("Invalid booking data!");
    }
    if (!booking.isValidDates()) {
        throw new InvalidBookingException("Check-out must be after check-in!");
    }
    if (!isAvailable(...)) {
        throw new InvalidBookingException("Booking #... rejected — accommodation not available!");
    }
    ...
}
```
Three guard clauses validate the booking before persisting; each throws the custom exception with a specific message.

**(C) Catching — Transaction #1 (no error)**

File: `Main.java:39-44`
```java
try {
    repo.addBooking(b1);
    System.out.println("[SUCCESS] Booking #101 created.");
} catch (BookingRepository.InvalidBookingException e) {
    System.out.println("[ERROR] " + e.getMessage());
} finally {
    System.out.println("[FINALLY] Transaction #1 completed.");
}
```
The `finally` block always runs, demonstrating resource cleanup (even though there are no I/O resources here).

**(D) Catching — Transaction #2 (invalid dates)**

File: `Main.java:49-57`
```java
try {
    repo.addBooking(b2);
} catch (BookingRepository.InvalidBookingException e) {
    System.out.println("[CAUGHT] " + e.getMessage());
    System.out.println("[RECOVERY] System continued safely.");
} finally {
    System.out.println("[FINALLY] Transaction #2 completed.");
}
```
The exception is caught, an error message is printed, and the system continues execution — this demonstrates **graceful recovery** rather than a crash.

**(E) Catching — Transaction #3 (null booking)**

File: `Main.java:59-64`
```java
try {
    repo.addBooking(null);
} catch (BookingRepository.InvalidBookingException e) {
    System.out.println("[CAUGHT] " + e.getMessage());
} finally {
    System.out.println("[FINALLY] Transaction #3 completed.");
}
```

**Exception Flow Summary:**

| Transaction | Trigger | throws? | caught? | finally? | System State |
|---|---|---|---|---|---|
| #1 | Valid booking | No | No | Yes | Booking added |
| #2 | Invalid dates | Yes | Yes | Yes | Booking rejected, system continues |
| #3 | null booking | Yes | Yes | Yes | Booking rejected, system continues |

---

### 2.4 Anonymous Inner Class

**Theory:**
An anonymous inner class is a class without a name, defined and instantiated in a single expression. It is used to provide an immediate implementation of an interface or extension of a class, typically for one-time use (event handlers, callbacks, comparators). Syntax:

```java
InterfaceType ref = new InterfaceType() {
    @Override
    public void method() {
        // implementation
    }
};
```

It creates an instance of an unnamed subclass. Before Java 8 (lambdas), this was the primary way to pass behavior inline.

**Applied in this project:**

File: `Main.java:65-73`
```java
Displayable anonymousDisplay = new Displayable() {
    @Override
    public void display() {
        System.out.println("  [Anonymous] System has " + repo.getAllBookings().size() +
                " booking(s), " + repo.getAllUsers().size() + " user(s)");
    }
};
anonymousDisplay.display();
```

What happens here:
1. `Displayable` is a `@FunctionalInterface` with a single abstract method `display()`.
2. `new Displayable() { ... }` creates an **anonymous subclass** of `Displayable` (technically, an anonymous implementation of the interface).
3. The body overrides `display()` with custom logic that accesses `repo` (a local variable from the enclosing scope — this is a **capturing anonymous class**).
4. The instance is assigned to `anonymousDisplay` and invoked.

**Contrasted with Lambda (Main.java:77-81):**
```java
Displayable lambdaDisplay = () -> System.out.println(
    "  [Lambda] System has " + repo.getAllBookings().size() +
    " booking(s), " + repo.getAllUsers().size() + " user(s)");
lambdaDisplay.display();
```

| Aspect | Anonymous Inner Class | Lambda Expression |
|---|---|---|
| Syntax verbosity | High (6+ lines) | Low (1 line) |
| Compilation | Generates `Main$1.class` | `invokedynamic` at runtime |
| `this` scope | Refers to the anonymous instance | Refers to enclosing instance |
| Can access local vars | Yes (must be final/effectively final) | Yes (must be effectively final) |
| Works with any interface/class | Yes | Only `@FunctionalInterface` (single method) |

**Polymorphic use in `displayAllData`:**

File: `Main.java:87`
```java
repo.displayAllData(() -> System.out.println("\n[SYSTEM REPORT] Current System State"));
```

Here the lambda is passed as a `Displayable` argument. Inside `BookingRepository.displayAllData()` (`BookingRepository.java:276`), `reportStyle.display()` calls the lambda — a polymorphic invocation of the lambda through the `Displayable` interface reference.

---

## 3. Summary Table: Concepts × Locations

| Concept | Where Defined | Where Used |
|---|---|---|
| **Abstract class** | `Accommodation.java` — `abstract class` | Cannot be instantiated; `Hotel`, `Apartment`, `GuestHouse` extend it |
| **Abstract method** | `Accommodation.java:62` — `abstract String getType()` | Enforced in all 3 subclasses; called in `Accommodation.display()` |
| **Dynamic polymorphism** | `Hotel/Apartment/GuestHouse` — override `display()` | `BookingRepository` loop iterating `List<Accommodation>`, `Main.java` `instanceof` check |
| **Compile-time polymorphism** | 3x `calculatePrice` overloads, 2x `addBooking`, 3x `processPayment` | Resolved at compile time by parameter types |
| **Custom exception** | `BookingRepository.InvalidBookingException` | Thrown in `addBooking()`, caught in `Main.java` with `try-catch-finally` |
| **Anonymous inner class** | `Main.java:67-73` — `new Displayable() { ... }` | One-time `Displayable` implementation |
| **Lambda expression** | `Main.java:77-81` — `() -> ...` | Shorter syntax for `Displayable` implementation |
| **Polymorphic parameter** | `displayAllData(Displayable)` | Accepts anonymous class or lambda interchangeably |
