# Mood Journal — Project Overview
_Last updated: 2025-07-15_

## 1 Purpose
Provide a **privacy‑first, prompt‑driven mood journal** that lets users check in < 30 seconds a day, see trends, and share “Mood Wraps,” all without storing personal text in the cloud.

## 2 Audience
* **End‑users:** Students and early‑career professionals who value minimalist, ad‑light wellness apps.  
* **Contributors:** Internal team using ChatGPT 🅖 and Claude Code 🅒, plus external open‑source collaborators after v1 launch.

## 3 Product Vision
| Pillar | Description |
| ------ | ----------- |
| **Privacy** | 100 % local storage; no account needed. |
| **Speed** | Daily check‑in completed in ≤ 30 s. |
| **Insight** | Visual trends (7/30/90 days) and shareable monthly wrap‑ups. |
| **Respect** | Non‑intrusive monetisation: banner ad removal & prompt packs via one‑time IAP. |

## 4 MVP Scope (v 1.0.0)
1. Daily check‑in (prompt + mood scale + optional text/tags)  
2. Prompt engine with streak/miss logic  
3. Timeline list & trend charts (MPAndroidChart)  
4. Local‑only storage (Room + DataStore)  
5. Banner ads & $1.99 Pro unlock  
6. PNG export (“Mood Wrap”)  
7. Data export/delete (GDPR‑safe)

## 5 Out‑of‑Scope (v 1.x)
* Cloud sync / accounts  
* Subscriptions  
* Real‑time social features  
* Clinical/medical claims

## 6 Roadmap Snapshot
| Version | Planned Date | Theme |
| ------- | ------------ | ----- |
| `0.1.x` | Internal alphas | Skeleton & data layer |
| `0.2.x` | **Sprint 2 ✅** | Material3 + Espresso tests |
| `0.3.x` | **Sprint 3** | Voice Mode MVP |
| `0.5.x` | Open testing | Monetisation & export |
| `1.0.0` | Public launch | Polished MVP |
| `1.1+`  | Q4‑2025 | PDF reports, on‑device AI tagging |

## 7 Key Links
* **Design Kanban:** GitHub Projects → _Mood Journal – Roadmap_  
* **Figma (UI drafts):** _TBD_  
* **CI status badge:** ![CI](https://github.com/ippiki-ookami/moodjournal/actions/workflows/android.yml/badge.svg)

---

> _Keep this doc high‑level and user‑facing; deep technical details live in **IMPLEMENTATION_DETAILS.md**._
