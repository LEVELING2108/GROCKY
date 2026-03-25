# Contributing to GROCKY

First off, thank you for considering contributing to GROCKY! It's people like you that make GROCKY such a great project.

## Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues as you might find out that you don't need to create one. When you are creating a bug report, please include as many details as possible:

* **Use a clear and descriptive title**
* **Describe the exact steps to reproduce the problem**
* **Provide specific examples to demonstrate the steps**
* **Describe the behavior you observed and what behavior you expected**
* **Include screenshots if possible**
* **Include environment details (OS, Java version, Node version, etc.)**

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, please include:

* **Use a clear and descriptive title**
* **Provide a detailed description of the suggested enhancement**
* **Explain why this enhancement would be useful**
* **List some examples of how this enhancement would be used**

### Pull Requests

* Fill in the required template
* Follow the Java/TypeScript style guides
* Include comments in your code where necessary
* Update documentation as needed
* Test your changes thoroughly

## Development Setup

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

### Frontend

```bash
cd frontend
npm install
npm run dev
```

### Running Tests

```bash
# Backend
cd backend
./mvnw test

# Frontend
cd frontend
npm test
```

## Style Guides

### Java

* Follow Oracle's Java Code Conventions
* Use 4 spaces for indentation
* Use meaningful variable names
* Add Javadoc comments for public methods

### TypeScript/React

* Follow the existing code style
* Use 2 spaces for indentation
* Use TypeScript types/interfaces
* Add comments for complex logic

## Additional Notes

### Git Commit Messages

* Use the present tense ("Add feature" not "Added feature")
* Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
* Limit the first line to 72 characters or less
* Reference issues and pull requests liberally

### Code Review Process

Once you submit a PR, the maintainers will review your code and may request changes. Please be responsive to feedback and update your PR as needed.

## Questions?

Feel free to open an issue for any questions about contributing to this project.

Thank you for your contribution! 🎉
