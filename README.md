<div align="center">
  
  <img width = "200" alt="loop" src="https://github.com/user-attachments/assets/a8d154d6-f9ea-4843-92ef-48098e0342ad" />
  <p>Subscriptions made simple. Poweered by SolanaPay</p>

<br/>
<p align="center">
  <img src="https://img.shields.io/github/last-commit/amitrajeet7635/loopr?label=Last%20Commit&logo=git" alt="Last Commit" />
  <img src="https://img.shields.io/badge/Hackathon-purple" alt="Hackathon" />
  <img src="https://img.shields.io/badge/status-Prototype-orange" alt="Status" />
  <img src="https://img.shields.io/badge/version-0.1.0-blue" alt="Version" />
  <img src="https://img.shields.io/badge/stability-Prototype-lightgrey" alt="Stability" />
  <img src="https://vbr.nathanchung.dev/badge?page_id=amanna13.orbit&label=Views&logoColor=white&color=ff9900&style=plastic" alt="Active Nodes" />

  <img src = "https://img.shields.io/badge/Android-green?logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/-Blockchain-121212?logo=blockchaindotcom&logoColor=white" alt="Blockchain" />
</p>
</div>

----

Orbit is a shared on-chain money pool built for everyday people - friends, families, small teams or local businesses ! Whosoever manage expenses together. Just create a pod, add members, pool funds, pay each other,  automate payouts - *all powered by Flow + Forte*. Orbit makes collaborative finance feel effortles, it’s collaborative finance reinvented. Whether roommates splitting rent or a vendor paying staff across town, pods keep money pooled, trackable, and instantly payable.

## Live Demos & Deliverables
[![Watch the video](https://img.youtube.com/vi/LYZbpNVmFv0/0.jpg)](https://www.youtube.com/watch?v=LYZbpNVmFv0)

## 🎯 Objective

### What problem does our project solve?
Managing shared expenses in groups is messy and unreliable. Friends, teams, communities, and creators often pool funds for events, savings, payouts, shared expenses, or micro-economies — but current systems rely on:
- Manual money splits on apps.
- Spreadsheets
- Trust-based settlement
- Centralized wallets
- Zero transparency on who paid, who didn’t, and where money went
There is no automated, trustless, and programmable system that can manage group-based pooled funds with scheduled payouts, role-based controls, and on-chain auditability.

Orbit solves this by turning every pooled fund into a **_“Pod”_** — a _programmable shared wallet_ on **Flow with built-in Forte-powered automations**.

### Who does it serve?

Orbit is built for any group that needs structured, transparent, programmable money flows, including:
- 🧑‍🤝‍🧑 Friends splitting recurring bills
- 🧑‍🎨 Creator communities distributing revenue to collaborators
- 👥 DAOs and micro-DAOs managing group treasury
- 🧑‍🏫 College clubs managing event funds
- 🧑‍💼 Small teams sharing operational budgets
- 🎮 Gaming guilds pooling in-game earnings
- 🛍️ Apartment societies collecting maintenance fees

Anyone who deals with shared money benefits !!

### Real-World Use Case:

Let’s imagine a street-side food vendor. He runs a small Taco and Burger stall. His brother runs another stall 20 km away, and both have 2–3 employees. Today, how they operate (the pain):
- Employees send expenses
- Cash is split manually
- Supplies are bought from different vendors
- Payments are uneven and tracking is messy
- When multiple people contribute for bulk items, someone has to front the money

#### Now see how they operate with Pods: 

1. *The “Daily Sales Pod”* - Both brothers share a pod. Every time a stall earns, they deposit into the Pod using our app. Each brother can see the live balance. At the end of the day, the Pod auto-releases 30% to employees, 20% to inventory vendor, and the rest to the brothers — all via scheduled Flow Actions.
- [x] No manual splitting. No confusion. No trust issues.

2. *The Employees’ “Salary Pod”* - Employees from both stalls are added as members. At the end of each day, the system logs contributions (sales). On Sundays at 10 PM, salary is auto-released proportionally.
- [x] No envelopes. No cash handling mistakes. Complete transparency.

3. *You as a person went to eat with your roommates* - You and your friend visit the street vendor for dinner. You both want to split the $20 bill evenly. But instead of using expense splitting apps, or paying one person and settling later, you:
- [x] Open the Roommates Pod you share with your flatmates
- [x] Add a “Food Expense” entry
Our system automatically splits the amount among all members. The Pod pays the vendor’s Pod directly. Funds move instantly through Flow Actions. Everyone’s balances update automatically — no tracking needed. Your friend doesn’t even need the vendor’s number. The Pod-to-Pod transaction handles everything.

> From tiny food stalls to shared apartments to small teams — people already behave like groups managing money. But tools haven't caught up. Pods make this coordination possible built for the real world — not just banks.

### Why We Chose This Problem ?
Money in the real world rarely moves alone - it moves between people, within small groups, and across informal networks. But the systems we use today (UPI, cash, banking apps, wallets) are built for individual transactions, not shared financial coordination.

1️⃣ *Real-world financial behavior is group-based* - but tools are not. Roommates paying rent, friends splitting bills, vendors paying employees, families sharing expenses, small stalls coordinating daily earnings - these are everyday patterns. Yet people still use screenshots, WhatsApp groups, spreadsheets, or manual cash settling.
This gap is huge, underserved, and increases friction every single day.

2️⃣ *Micro-businesses form the backbone of the economy - but lack automation* - Street vendors, gig workers, corner shops, delivery teams, and stall owners run entirely on trust and manual bookkeeping. Automation, transparency, and programmable payouts should not be a luxury only large companies enjoy. Pods give small groups the same capabilities that enterprises have — but simplified, safe, and instant.

## 🧠 Our Approach
We solved this by building Orbit Pods on top of Flow’s resource-oriented Cadence architecture, where each pod is a secure on-chain resource that enforces shared ownership, member permissions, and fund safety by design. To automate real-world financial behaviors, we integrated Forte Flow Actions - using Sink Actions for seamless incoming deposits, Source Actions for controlled outgoing transfers or payouts, and Scheduled Actions to power recurring settlements like daily cashouts, weekly revenue splits, or monthly employee distributions. 
The backend acts as the orchestrator: it receives authenticated user requests from the mobile app, constructs Cadence transactions, wraps them into Flow Actions payloads, executes them on-chain, and returns verified results back to the client. 
On the mobile side, we used Web3Auth for frictionless login, local caching for instant session recovery, and a clean Kotlin UI that hides all blockchain complexity behind simple actions like “Create Pod,” “Join with QR,” “Deposit,” or “Payout.” This layered architecture allows Orbit to feel like a simple shared wallet app while securely running on programmable smart contracts and automated on-chain financial pipelines.

### Architecture
<img width="600" height="1938" alt="12" src="https://github.com/user-attachments/assets/1cf1058a-3e67-4469-bd0e-e74ec048b67f" />
<img width="700"  alt="End-to-End flow diagram" src="https://github.com/user-attachments/assets/ab95d2eb-b55b-4c43-a7a3-6be4da2d95d1" />
<img width="300" height="3251" alt="13" src="https://github.com/user-attachments/assets/b3f126bb-c831-4b27-8658-fe7cceb3260e" />
<img width="400" height="2536" alt="14" src="https://github.com/user-attachments/assets/e4e414a1-faeb-4d36-8e25-d7df5c3086b5" />

### How it looks (UI Walkthrough)!


-----------------------
Made with ❤️ 

<div align="center"> <strong>We're actively building, debugging, and prototyping —<br>drop a ⭐, share your feedback, or just vibe with us!</strong> <br/><br/> <img src="https://github.com/Anmol-Baranwal/Cool-GIFs-For-GitHub/assets/74038190/491e3e44-11a0-487a-b07b-717f677bbe4a" width="170" /> </div>
