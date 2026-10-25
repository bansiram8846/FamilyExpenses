---
name: FamilyWise AI System
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#45464d'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#006a61'
  on-secondary: '#ffffff'
  secondary-container: '#86f2e4'
  on-secondary-container: '#006f66'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#002113'
  on-tertiary-container: '#009668'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#89f5e7'
  secondary-fixed-dim: '#6bd8cb'
  on-secondary-fixed: '#00201d'
  on-secondary-fixed-variant: '#005049'
  tertiary-fixed: '#6ffbbe'
  tertiary-fixed-dim: '#4edea3'
  on-tertiary-fixed: '#002113'
  on-tertiary-fixed-variant: '#005236'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display-currency:
    fontFamily: Inter
    fontSize: 36px
    fontWeight: '700'
    lineHeight: 44px
    letterSpacing: -0.02em
  display-currency-mobile:
    fontFamily: Inter
    fontSize: 30px
    fontWeight: '700'
    lineHeight: 38px
    letterSpacing: -0.02em
  headline-lg:
    fontFamily: Inter
    fontSize: 28px
    fontWeight: '600'
    lineHeight: 36px
    letterSpacing: -0.01em
  headline-md:
    fontFamily: Inter
    fontSize: 22px
    fontWeight: '600'
    lineHeight: 28px
  headline-sm:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '600'
    lineHeight: 24px
  title-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '600'
    lineHeight: 22px
  body-lg:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  body-md:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '400'
    lineHeight: 20px
  body-sm:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '400'
    lineHeight: 16px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '500'
    lineHeight: 20px
  label-md:
    fontFamily: Inter
    fontSize: 12px
    fontWeight: '500'
    lineHeight: 16px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '600'
    lineHeight: 14px
    letterSpacing: 0.04em
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  gutter: 1rem
  gutter-mobile: 0.75rem
  margin: 1.25rem
  margin-mobile: 1rem
  space-xs: 0.25rem
  space-sm: 0.5rem
  space-md: 1rem
  space-lg: 1.5rem
  space-xl: 2rem
---

## Brand & Style

The design system is engineered for multi-generational Indian households collaborating on financial clarity, mutual budgeting, and shared domestic accounts. The brand tone is calm, authoritative, radically transparent, and emotionally steady—avoiding the anxiety often triggered by traditional consumer personal finance apps.

The aesthetic blends **Modern Corporate Material Design 3 (MD3)** with **Tactile Functionalism**:
- **Generational Accessibility:** High contrast, legible typography, prominent touch targets (minimum 48dp), and clear cognitive hierarchy suitable for young earners, spouses, and elder parents alike.
- **Calm FinTech Rigor:** Visual friction is deliberately minimized. Deep navy conveys institutional safety, while crisp teal and mint emphasize forward momentum and growth.
- **Contextual Intelligence:** Transparent algorithmic indicators distinguish deterministic operations ("Local Rule Engine") from generative or server-side intelligence ("Online AI Parsing"), reinforcing user trust through accountability.

## Colors

The palette balances institutional integrity with crisp, modern utility:

- **Primary (`#0F172A`):** Deep Financial Navy anchor for high-emphasis typography, primary action containers, and key functional structures. 
- **Secondary (`#0D9488`):** Deep Trustworthy Teal for active interactive states, primary analytical visualizers, and brand touchpoints.
- **Tertiary (`#10B981`):** Crisp Mint for positive balances, automated savings goals, and success states.
- **Surface Foundations:** Background canvas sits at `#F8FAFC`, stepping up to `#F1F5F9` for nested surface containers and cards, bounded by 1px subtle structural borders (`#E2E8F0`).
- **Semantic Guardrails:**
  - **Budget Advisory (75% Threshold):** Warm Amber (`#F59E0B`) deployed strictly for caution, runway depletion warnings, and pending approvals.
  - **Deficit / Overdraft:** Soft Crimson (`#EF4444`) strictly reserved for hard limits, negative cash flow, and overspent categories. Never use red for decorative elements or non-critical actions.

## Typography

The typography system relies on **Inter**, selected for its neutral geometric clarity and robust OpenType tabular lining figures (`font-variant-numeric: tabular-nums`).

### Currency Formatting Rules
- All balance metrics and transaction amounts must implement tabular figures to guarantee vertical alignment in lists, tables, and comparison cards.
- Formatting strictly follows the **Indian Numbering System** with standard comma placement for thousands, lakhs, and crores (e.g., `₹24,650`, `₹1,85,400`, `₹12,40,000`).
- The Indian Rupee symbol (`₹`) is rendered at 85% opacity of the accompanying amount string to anchor prominence to the numeric value rather than the glyph.

## Layout & Spacing

The layout adapts MD3 responsive specifications optimized primarily for single-hand mobile usage while scaling cleanly across foldable screens and tablets:

- **Mobile Canvas (360dp – 599dp):** Single column fluid container with `16dp` outer margins and `12dp` item gutters. All primary CTAs and input triggers sit within thumb-reach zones in the lower half of the screen.
- **Foldable & Tablet Canvas (600dp+):** Dual-pane structure (Master-Detail or Ledger/Insight Split). Content width is constrained to `840dp` max-width when centered in single-pane views.
- **Rhythm:** Built strictly upon an **8dp baseline grid** (with `4dp` half-step increments applied exclusively to icon padding, chips, and micro-badges).

## Elevation & Depth

Visual depth is achieved through **tonal container layering** complemented by diffuse, low-contrast shadows rather than heavy drop shadows:

- **Surface Level 0 (Canvas):** `#F8FAFC` — base backdrop for the entire screen.
- **Surface Level 1 (Default Cards, App Bar):** `#FFFFFF` with a 1px border (`#E2E8F0`) and `box-shadow: 0 1px 3px 0 rgba(15, 23, 42, 0.05), 0 1px 2px -1px rgba(15, 23, 42, 0.05)`.
- **Surface Level 2 (Elevated Cards, Bottom Sheets):** `#FFFFFF` with `box-shadow: 0 4px 6px -1px rgba(15, 23, 42, 0.07), 0 2px 4px -2px rgba(15, 23, 42, 0.05)`.
- **Surface Level 3 (Floating Action Button & Modals):** `#0F172A` (FAB) or `#FFFFFF` (Modals) with `box-shadow: 0 10px 15px -3px rgba(15, 23, 42, 0.12), 0 4px 6px -4px rgba(15, 23, 42, 0.08)`.

## Shapes

The design system utilizes **Rounded (Tier 2)** geometry to balance modern MD3 softness with financial structure:

- **Small Components (Buttons, Input Fields, Badges):** `8px` (`rounded-md`).
- **Cards & Ledger Containers:** `16px` (`rounded-lg`) up to `20px` for featured balance cards.
- **Interactive Modals & Bottom Sheets:** `24px` top-edge corner radius (`rounded-xl`).
- **Interactive Tags, Member Avatars & Filter Chips:** Full capsule / pill radii (`9999px`).

## Components

### Buttons
- **Primary:** Filled `#0F172A` with `#FFFFFF` text. Height: `48dp`, corner radius: `12dp`. Touch target minimum: `48x48dp`.
- **Secondary / Action:** Tinted `#F1F5F9` background, `#0F172A` text, 1px border `#CBD5E1`.
- **Floating Action Button (FAB):** Elevated MD3 circular or squircle container (`56x56dp`) positioned prominently above the 5-tab bar. Styled in vibrant `#0D9488` with a crisp `#FFFFFF` icon for "Add Expense".

### Cards (MD3 Architectural Ledger)
- Background `#FFFFFF`, border `1px solid #E2E8F0`, corner radius `16dp`.
- Header area contains title, member avatar stack, and metadata tag.
- Numeric balances use tabular bold figures with clear split-attribution lines (e.g., "Paid by Papa", "Split equally with 3 members").

### Inputs & Form Fields
- Outlined style with default `1px solid #CBD5E1` border and filled `#F8FAFC` background.
- Focus state shifts border to `2px solid #0D9488` with `#FFFFFF` background.
- Number fields default to numeric keyboards with explicit Indian Rupee prefix (`₹`) anchored inside the leading box.

### Chips & Badges
- **Category & Member Filter Chips:** Height `32dp`, pill-shaped (`9999px`), light background `#F1F5F9`, `#334155` text. Active state: `#0F172A` background with `#FFFFFF` text.
- **Offline Mode Indicator:** Compact capsule badge displaying an offline cloud icon with `#64748B` border and subtle pulsating amber dot when updates are pending queue sync.
- **AI Attribution Badges:**
  - *Local Rule:* Low-contrast `#F1F5F9` pill with `#475569` text and subtle gear icon ("Categorized by Local Rule").
  - *Online AI:* Soft teal tint (`#CCFBF1`) pill with `#0F766E` text and sparkle glyph ("Analyzed by FamilyWise AI").

### Navigation Bar
- 5-tab fixed bottom bar (`64dp` height) anchored over `#FFFFFF` with a 1px top border `#E2E8F0`.
- Tabs: Home (Dashboard), Passbook (Shared Ledger), Analytics (Budget & Insights), Family (Members & Allocations), Settings.
- Centered FAB sits anchored or partially notched above the bar for friction-free logging.