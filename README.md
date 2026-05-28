# PokemonTest

This project uses the [PokeAPI (GraphQL)](https://beta.pokeapi.co/graphql/v1beta) to display information about Pokémon.

## Architecture

This project follows the **MVVM (Model-View-ViewModel)** architecture pattern, leveraging modern Android architecture components to ensure a clear separation of concerns, testability, and maintainability.

- **UI Layer (`ui`)**: Built entirely with Jetpack Compose. ViewModels manage the UI state, handle user interactions, and expose state to the Composables.
- **Data Layer (`data`)**: Repositories handle data operations. They interact with the Apollo GraphQL client to fetch data from the network and provide it to the ViewModels. Paging 3 is utilized here to paginate large data sets seamlessly.

## Libraries Used

The project relies on a modern Android tech stack:

- **[Jetpack Compose](https://developer.android.com/jetpack/compose)**: Modern toolkit for building native UI in a declarative manner.
- **[Hilt](https://dagger.dev/hilt/)**: Dependency injection library for Android that reduces the boilerplate of manual dependency injection.
- **[Apollo GraphQL](https://www.apollographql.com/docs/kotlin/)**: GraphQL client for Kotlin, used to communicate with the PokeAPI.
- **[Paging 3](https://developer.android.com/topic/libraries/architecture/paging/v3-overview)**: Helps load and display small chunks of data at a time to improve network and system resource consumption.
- **[Coroutines & Flow](https://kotlinlang.org/docs/coroutines-overview.html)**: For managing background threads, asynchronous operations, and reactive data streams.
- **[Navigation Compose](https://developer.android.com/jetpack/compose/navigation)**: For handling navigation between different screens within Jetpack Compose.

### Testing Libraries
- **JUnit 4**: For local unit testing.
- **MockK**: For mocking dependencies in unit tests.
- **Turbine**: A small testing library for verifying Kotlin Flows.
- **Kotlinx Coroutines Test**: For controlling execution of coroutines during testing.

## How to Build

### Steps to Build and Run
From the terminal using the Gradle Wrapper:

```bash
#DownloadApolloSchema
./gradlew downloadApolloSchemaFromIntrospection

# To build a debug APK
./gradlew assembleDebug

# To run unit tests
./gradlew test
```
