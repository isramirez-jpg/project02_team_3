# CST 338 Project 2 — E-Commerce App

**Team Name:** Cache Me Outside

**Team Members:** Ha Nguyen, Isabel Ramirez, Miguel Quezada
<!--
CST 338 Project 2 — README template.
Copy this file into the ROOT of your team's repository as README.md and keep it current.
This README is your project dashboard: it is the first thing the instructor reads when
grading, and a working, up-to-date README is part of your integration score.

GitHub Issues are your LIVE tracker — every slice task, enhancement, and scope decision is
an Issue: assigned to its owner, labeled (slice-1, testing, enhancement, will-not-do,
extra-credit), and closed by a PR via "Closes #N". The tables below link into those Issues
and PRs. Replace every <placeholder> and delete this comment before you submit.
-->



## Team & Slice Ownership
| Slice | Owner | GitHub username | Issues | Branch(es)                                                                                                                                                                                              | PR(s)                        | Enhancement chosen | Status         |
|-------|-------|-----------------|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------|--------------------|----------------|
| 1 — Accounts | Miguel Quezada | mquezada2026 | #8, #9, #10 | miguel/initial-commit-login-scene-skeleton, miguel/registered-users-fxml-scene, miguel/dao-login-scene-controller, miguel/ai-code-review-fix, miguel/add-master-stylesheet, miguel/add-password-reset, miguel/revise-main-scene-buttons | #12, #13, #17, #19, #24, #27 | TableView/ListView, custom reusable FXML component | complete |
| 2 — Catalog | Ha Nguyen | hanguyen1979 |#5, #6, #7 | Ha/catalog skeleton                                                                                                                                                                                     | PR #14, #20, #22             |Notifications / Alerts | in-progress    |
| 3 — Cart & Checkout | Isabel Ramirez | isramirez-jpg | #1 #2 #3 #4 | isabel/cart-database-dao, isabel/cart-clean-integration                                                                                                                                                 | #15, # 18, #21               | JavaFX TableView<CartItem> | in-progress    |
| 4 — Order History & Management | | | |                                                                                                                                                                                                         |                              | | WILL NOT DO    |
| 5 - Reviews & Ratings | | | |                                                                                                                                                                                                         |                              | | WILL NOT DO    |

_Status values: planned · in-progress · complete_

## WILL NOT DO (declared scope cuts)
_Slices and beyond-scope items we are consciously NOT building. Move an item to a tracked
Issue if the team later decides to attempt it for extra credit._

- Slice 4 — <name>: not building (team of 3).
- Slice 5 - <name>: not building (team of 3).
- <Slice 1>: addresses — out of scope.
- <Slice 1>: extra roles — out of scope.
- <Slice 2>: categories/filters — out of scope.
- <Slice 2>: image handling — out of scope.
- <Slice 2>: pagination — out of scope.
- <Slice 3>: coupons — out of scope.
- <Slice 3>: tax/shipping — out of scope.
- <Slice 3>: payment-API stub — out of scope.

## Code Review Log
| PR   | Author         | Human reviewer(s)               | AI review (link)                                             | Outcome   |
|------|----------------|---------------------------------|--------------------------------------------------------------|-----------|
| #12  | Miguel Quezada | Isabel Ramirez                  |                                                              | Approved  |
| #13  | Miguel Quezada | Ha Nguyen                       |                                                              | Approved  |
| #14  | Ha Nguyen      | Miguel Quezada                  | <link or commit>                                             | Approved  |
| #15  | Isabel Ramirez | Miguel Quezada                  |                                                              | Approved  |
| #18  | Isabel Ramirez | Ha Nguyen                       |                                                              | Approved  |
| #19  | Miguel Quezada | Isabel Ramirez                  | https://claude.ai/chat/464cdb67-61f3-4ba3-8276-8bb66ab95911  | Approved  |
| #20  | Ha Nguyen      | Isabel Ramirez & Miguel Quezada |                                                              | Approved  |
| #21  | Isabel Ramirez | Miguel Quezada  & Ha Nguyen     | https://claude.ai/share/40911211-0ae5-4287-8c93-4bad04e4e800 | Approved  |  
| #22  | Ha Nguyuen     | Miguel Quezada  & Isabel Ramirez|https://github.com/isramirez-jpg/project02_team_3/pull/22     | Pending  |

## AI Usage Log

- AI-drafted tests: <link to TESTING.md / commit> — per owner.
- AI code review: [PR #21](https://github.com/isramirez-jpg/project02_team_3/pull/21) — feedback reviewed and applicable checkout error-handling and transaction-safety recommendations implemented by Isabel Ramirez.
- AI code review: [PR #19](https://github.com/isramirez-jpg/project02_team_3/pull/19) — Fixed item F8 in AI Code Review feedback by Miguel Quezada.
- AI code review: [PR #22](https://github.com/isramirez-jpg/project02_team_3/pull/22) — Review PR for ha/catalog-add-product branch

## Extra Credit Log
| Item | Who | Evidence (Issue/PR) |
|------|-----|---------------------|
|  | | |


## App Run Instructions
1. Clone the repo and open it in IntelliJ IDEA.
2. Run the app via Gradle. Click on the elephant icon on the top right corner of IntelliJ IDEA,
   then open the Tasks folder, then open the application folder, then double click on run.
3. When the app launches, you will see the login screen.
4. Use one of the test logins below to log in or register and create an account and explore the app.
5. Users with the **ADMIN** role can add products to the catalog, and view other management screens.
6. Users with the **USER** role can only view products and add them to their cart.
7. If you forget your password, click on the "Forgot Password" link and answer the security question to reset your password.

## Test Logins

| Username | Password | Role | First Name | Last Name | Security Question | Security Answer |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `Mickey2026` | `Bolaochotintandd!` | **ADMIN** | Mickey | Mouse | What is your favorite musical artist? | My Chemical Romance |
| `Queenpenelope` | `Guavakiwid8!` | **USER** | Anne | Hathaway | What is your favorite musical artist? | Pearl Jam |
| `Laurapau26` | `Mibandatocarock7!` | **USER** | Laura | Pausini | What is your favorite musical artist? | Linkin Park |

## Build & Run
```
./gradlew run        # launch the app
./gradlew test       # run the test suite
```
Requirements: JDK <version>, JavaFX <version>. Any setup notes go here.
