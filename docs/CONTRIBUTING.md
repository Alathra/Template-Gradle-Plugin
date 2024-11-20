## Pull Requests

We use the [GitHub flow](https://guides.github.com/introduction/flow/) for contributions.

1. Fork the repository.
2. Create a new branch for each feature, fix or improvement.
3. Send a pull request from each feature branch to the **main** branch.

It is very important to separate new features or improvements into separate feature branches, and to send a pull request for each branch. This allow us to review and pull in new features or improvements individually.

## Specifications

### Commit Messages

All commit messages should adhere to the [Conventional Commits specification](https://conventionalcommits.org/).

#### Commit Types

- API relevant changes
    * `feat` Commits, that adds a new feature
    * `fix` Commits, that fixes a bug/issue
- `refactor` Commits, that rewrite/restructure your code, however does not change any API behaviour
    * `perf` Commits are special `refactor` commits, that improve performance
- `style` Commits, that do not affect the meaning (white-space, formatting, missing semi-colons, etc)
- `test` Commits, that add or correct existing tests
- `docs` Commits, that affect documentation only
- Tooling relevant changes
    * `build` Commits, that affect build components like build tool, dependencies, project version, ...
    * `ci` Commits, that affect CI configuration files and scripts
- `revert` Commits, that revert previous commits
- `chore` Miscellaneous commits without production code change, e.g. modifying `.gitignore`

### Versioning

Our project adheres to the [Semantic Versioning 2.0.0 specification](https://semver.org/) for versioning.

### Style Guide

Large refactors breaking the style defined in the `.editorconfig` file are frowned upon. 
When contributing ensure your code follows existing conventions and styling. (*Any issues that arise will be noted in your pull request.*)

## Licensing

All contributors agree to adhere to the terms and conditions of our [Contributor License Agreement (CLA)](CONTRIBUTOR_LICENSE_AGREEMENT.md).