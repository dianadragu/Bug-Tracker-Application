## Project Structure

The project is organized into modular packages to ensure separation of concerns:

* **`commands`**: Command classes, command management logic, and the central error handler.
* **`database`**: The application's database simulation and the enum for application periods.
* **`entities`**: The core domain models, subdivided into:
  * **`users`**: User classes implementing a common `Common` interface.
  * **`tickets`**: Ticket entities and an `observers` sub-package managing the notification system for ticket property changes.
  * **`observer`**: Contains the core `Observer` interface and `Subject` class.
  * **`milestone`**: Milestone entities, status enums, and milestone-specific observers.
  * **`fileio`**: Data mapping classes that parse JSON input files into Java objects.
  * **`reports`**: Implements Strategy and Visitor patterns for report-building logic (includes a `performance` sub-package).
  * **`main`**: Contains the `App` entry point and `AppRunner`, which handles execution, command routing, and updates.
  * **`search`**: Logic for filter-based search commands (utilizes the `filters` sub-package).
  * **`utils`**: Helper classes for generating JSON output nodes, math calculations, and `LocalDate` manipulation.

### Technologies Used
* **Java**: Core language.
* **Jackson**: For JSON serialization and deserialization (`@JsonTypeInfo`, `@JsonSubTypes`).
* **Lombok**: Used for boilerplate reduction (Getters, Setters, Constructors) and implementing the `@SuperBuilder` pattern to support deep inheritance hierarchies.

### Design Patterns Implemented
* **Singleton**: Global database access.
* **Command**: Encapsulating requests as objects.
* **Factory**: Instantiating commands and search filters.
* **Observer**: Event-driven notification system.
* **Strategy**: Dynamic algorithm selection for searches and performance calculations.
* **Visitor**: Role verification and report generation across varied object types.

---

## Implementation Stages

### 1. Application Skeleton & Database
The foundation relies on mapping JSON inputs accurately and establishing a single source of truth for data.
* **Input Deserialization**: `InputLoader` populates `UserInput` and `CommandInput` lists. Polymorphism is handled automatically by Jackson based on the "role" field.
* **Execution Flow**: `AppRunner` separates execution logic from the `App` class. It processes commands, initializes the database, and ensures a clean state (`clearDatabase`) between runs.
* **AppDatabase**: Implemented as a **Singleton** to provide global access to shared states (users, milestones, tickets, app periods) without passing instances around unnecessarily.


### 2. Core Entities (`Ticket` & `User`)
* **Users**: Both Managers and Developers track milestones they interact with via a shared parent structure.
* **Tickets**: Maintain separate lists for user comments and historical changes (`ticketHistory`). Methods are defined for state transitions (priority, status, expertise needed).
* **Instantiation**: Used a hybrid **Factory/Visitor** approach with Lombok's `SuperBuilder` to handle the varying parameters required by different ticket and user types. 

### 3. Command Execution System

* **Command Pattern**: A unified `Command` interface with an `execute()` method. An `Invoker` (managed by `AppRunner`) executes these commands, keeping the door open for future features like "Undo".
* **Command Factory**: Eliminates massive switch statements by returning the exact command instance based on a string name.
* **Visitor Pattern for Validation**: `ErrorHandler` validates commands, but because different commands accept different user roles, it acts as a Visitor. Commands implement `accept(ErrorHandler)` to define their allowed roles and trigger validation.

### 4. Advanced Logic & Specific Features

* **Notifications (Observer Pattern)**: 
  
  Implemented generic `Subject<T>` and `Observer<T>` interfaces to avoid duplicating code between Milestone and Ticket notifications. Entities maintain their own observers and fire updates when state changes occur.
* **Search & Filters (Strategy Pattern)**: 
  
  A `FilterFactory` provides a list of filtering strategies based on the entity type (Dev/Ticket). A `SearchContext` then applies these generic strategies cleanly over the data sets.
* **Report Generation**: 
  * **Visitor Pattern**: Used for *Risk*, *Customer Impact*, and *Resolution Efficiency* reports, as they require navigating different ticket types with distinct parameters.
  * **Strategy Pattern**: Used for *Performance* reports, allowing the calculation formula to swap dynamically based on a developer's expertise level.

