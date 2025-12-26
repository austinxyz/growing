# Frontend UI Design Guide - Best Practices

> **Purpose**: Design patterns and best practices for building visually appealing, consistent UI
> **Reference Implementation**: SystemDesignCases.vue (Phase 5.2)
> **Last Updated**: 2025-12-26

## Table of Contents
- [Design Philosophy](#design-philosophy)
- [Color System](#color-system)
- [Layout Patterns](#layout-patterns)
- [Component Styling](#component-styling)
- [Interactive Elements](#interactive-elements)
- [Code Examples](#code-examples)

---

## Design Philosophy

### Core Principles

1. **Visual Hierarchy**: Use gradients, shadows, and colors to establish clear hierarchy
2. **Consistency**: Maintain consistent color families and gradient patterns across the app
3. **Feedback**: Provide visual feedback for all interactive elements (hover, active, disabled states)
4. **Accessibility**: Ensure sufficient contrast ratios (white text on colored backgrounds)
5. **Progressive Enhancement**: Start with flat design, layer on gradients and shadows

### When to Use Color

✅ **DO Use Color For**:
- Page headers and section headers
- Card headers and primary actions
- Status indicators (difficulty, tags, badges)
- Active/selected states
- Hover effects

❌ **DON'T Overuse**:
- Plain text content (keep readable)
- Background colors for large text areas
- Too many competing gradients in one view

---

## Color System

### Gradient Backgrounds

Use Tailwind's gradient utilities for depth and visual interest:

```vue
<!-- Page-level gradient -->
<div class="bg-gradient-to-br from-blue-50 via-purple-50 to-pink-50">

<!-- Header gradient -->
<div class="bg-gradient-to-r from-blue-600 to-purple-600">

<!-- Card header gradient -->
<div class="bg-gradient-to-r from-indigo-500 to-purple-500">
```

**Pattern**: Use 2-3 color stops for smooth transitions
- `from-{color}-{shade}`: Start color
- `via-{color}-{shade}`: Middle color (optional, creates 3-color gradient)
- `to-{color}-{shade}`: End color

**Direction Guidelines**:
- `to-r`: Left to right (headers, horizontal elements)
- `to-br`: Top-left to bottom-right (full page backgrounds)
- `to-b`: Top to bottom (vertical gradients for accent bars)

### Color Families

#### Primary Palette (Blue-Purple-Pink)
```css
/* Page backgrounds */
from-blue-50 via-purple-50 to-pink-50

/* Headers and important elements */
from-blue-600 to-purple-600
from-indigo-600 to-purple-600
from-indigo-500 to-purple-500
```

#### Status Colors (Difficulty Levels)
```vue
<!-- Easy: Green gradient -->
<span class="bg-gradient-to-r from-green-400 to-emerald-500 text-white">

<!-- Medium: Yellow gradient -->
<span class="bg-gradient-to-r from-yellow-400 to-orange-500 text-white">

<!-- Hard: Red gradient -->
<span class="bg-gradient-to-r from-red-400 to-pink-500 text-white">
```

#### Semantic Colors (Card Categories)
```vue
<!-- Requirement: Blue -->
<div class="bg-gradient-to-br from-blue-50 to-white border-2 border-blue-200">

<!-- Components: Purple -->
<div class="bg-gradient-to-br from-purple-50 to-white border-2 border-purple-200">

<!-- NFR: Green -->
<div class="bg-gradient-to-br from-green-50 to-white border-2 border-green-200">

<!-- Entity: Yellow -->
<div class="bg-gradient-to-br from-yellow-50 to-white border-2 border-yellow-200">

<!-- API: Red -->
<div class="bg-gradient-to-br from-red-50 to-white border-2 border-red-200">

<!-- Object1: Indigo -->
<div class="bg-gradient-to-br from-indigo-50 to-white border-2 border-indigo-200">

<!-- Object2: Pink -->
<div class="bg-gradient-to-br from-pink-50 to-white border-2 border-pink-200">
```

### Gradient Text

For section headers without full background color:

```vue
<h3 class="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
  📖 Section Title
</h3>
```

**Classes Breakdown**:
- `text-transparent`: Makes text transparent to show gradient
- `bg-clip-text`: Clips gradient to text shape
- `bg-gradient-to-r`: Creates the gradient

---

## Layout Patterns

### Three-Column Layout

**Use Case**: Content management with list, metadata, and detail view

```vue
<template>
  <div class="h-screen flex flex-col">
    <!-- Header -->
    <div class="bg-gradient-to-r from-blue-600 to-purple-600 px-6 py-4">
      <h1 class="text-2xl font-bold text-white">Page Title</h1>
      <p class="text-xs text-blue-100 mt-1">Subtitle</p>
    </div>

    <!-- Main: Three columns -->
    <div class="flex-1 flex overflow-hidden">
      <!-- Left: List (Narrow) -->
      <div class="w-1/6 bg-white border-r border-gray-200 flex flex-col shadow-lg">
        <!-- List items -->
      </div>

      <!-- Middle: Metadata (Medium) -->
      <div class="w-1/4 bg-white border-r border-gray-200 p-4 overflow-y-auto">
        <!-- Metadata sections -->
      </div>

      <!-- Right: Detail (Wide) -->
      <div class="flex-1 bg-white p-4 flex flex-col overflow-hidden">
        <!-- Detail content -->
      </div>
    </div>
  </div>
</template>
```

**Width Ratios**:
- Narrow list: `w-1/6` (16.67%)
- Medium metadata: `w-1/4` (25%)
- Wide detail: `flex-1` (remaining ~58.33%)

### Card-Based Layout

**Use Case**: Grid of information cards

```vue
<div class="grid grid-cols-2 gap-4">
  <!-- Card -->
  <div class="bg-gradient-to-br from-blue-50 to-white rounded-xl border-2 border-blue-200 shadow-md hover:shadow-xl transition-all p-3">
    <!-- Card header -->
    <div class="flex items-center gap-2 mb-2">
      <div class="w-2 h-6 bg-gradient-to-b from-blue-400 to-blue-600 rounded-full"></div>
      <h4 class="text-sm font-bold text-blue-700">Card Title</h4>
    </div>
    <!-- Card content -->
    <div class="text-xs text-gray-700">Content...</div>
  </div>
</div>
```

**Grid Patterns**:
- `grid-cols-2`: 2 columns (50% each)
- `gap-4`: 1rem spacing between cards
- `row-span-2`: Card spans 2 rows (for taller content)

---

## Component Styling

### Headers and Titles

#### Page Header
```vue
<div class="bg-gradient-to-r from-blue-600 to-purple-600 shadow-lg px-6 py-4">
  <h1 class="text-2xl font-bold text-white">系统设计 - 典型案例实战</h1>
  <p class="text-xs text-blue-100 mt-1">掌握经典系统设计案例，提升架构设计能力</p>
</div>
```

**Key Classes**:
- `shadow-lg`: Large shadow for depth
- `text-white`: High contrast on colored background
- Subtitle in lighter shade (`text-blue-100`)

#### Section Header (with gradient text)
```vue
<h3 class="text-lg font-bold mb-3 text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-purple-600">
  📖 案例描述
</h3>
```

#### Subsection Header (with emoji)
```vue
<div class="flex items-center gap-2 mb-2">
  <div class="w-2 h-6 bg-gradient-to-b from-blue-400 to-blue-600 rounded-full"></div>
  <h4 class="text-sm font-bold text-blue-700">📋 Requirement</h4>
</div>
```

**Pattern**: Vertical accent bar + emoji + colored text

### Cards

#### Basic Card
```vue
<div class="bg-white rounded-lg shadow-md p-4">
  <!-- Content -->
</div>
```

#### Enhanced Card (with gradient background)
```vue
<div class="bg-gradient-to-br from-blue-50 to-white rounded-xl border-2 border-blue-200 shadow-md hover:shadow-xl transition-all p-3">
  <div class="flex items-center gap-2 mb-2">
    <div class="w-2 h-6 bg-gradient-to-b from-blue-400 to-blue-600 rounded-full"></div>
    <h4 class="text-sm font-bold text-blue-700">Card Title</h4>
  </div>
  <div class="text-xs text-gray-700 whitespace-pre-wrap leading-relaxed">
    Card content...
  </div>
</div>
```

**Enhancement Layers**:
1. Gradient background: `from-{color}-50 to-white`
2. Colored border: `border-2 border-{color}-200`
3. Shadow: `shadow-md` → `hover:shadow-xl`
4. Transitions: `transition-all`
5. Accent bar: `w-2 h-6 bg-gradient-to-b`

### Badges

#### Rounded Badge (with gradient)
```vue
<span :class="[
  'px-3 py-1 text-xs rounded-full font-semibold shadow-md',
  difficulty === 'EASY' ? 'bg-gradient-to-r from-green-400 to-emerald-500 text-white' :
  difficulty === 'MEDIUM' ? 'bg-gradient-to-r from-yellow-400 to-orange-500 text-white' :
  'bg-gradient-to-r from-red-400 to-pink-500 text-white'
]">
  {{ difficultyText(difficulty) }}
</span>
```

**Pattern**: Conditional gradient based on value

#### Tag Badge
```vue
<span class="px-3 py-1 text-xs rounded-full bg-white/90 text-indigo-700 font-medium shadow-sm">
  🏢 Google
</span>
```

**Pattern**: White background with opacity (`bg-white/90`)

### Buttons

#### Primary Action Button
```vue
<button :class="[
  'px-4 py-2 rounded-lg transition-all text-sm font-bold shadow-md',
  isEditMode
    ? 'bg-gradient-to-r from-red-500 to-pink-500 text-white hover:from-red-600 hover:to-pink-600 hover:shadow-lg'
    : 'bg-gradient-to-r from-green-500 to-emerald-500 text-white hover:from-green-600 hover:to-emerald-600 hover:shadow-lg'
]">
  {{ isEditMode ? '✓ 退出编辑' : '✏️ 开始答题' }}
</button>
```

**Key Features**:
- Gradient background
- Hover state: Darker gradient (`from-{color}-600`)
- Shadow enhancement on hover (`hover:shadow-lg`)
- Conditional colors based on state

---

## Interactive Elements

### Hover Effects

#### Card Hover
```vue
<div :class="[
  'p-4 border-b border-gray-100 cursor-pointer transition-all duration-200',
  isSelected
    ? 'bg-gradient-to-r from-blue-50 to-purple-50 border-l-4 border-l-blue-600 shadow-md'
    : 'hover:bg-gradient-to-r hover:from-gray-50 hover:to-blue-50 hover:shadow-sm'
]">
```

**Pattern**:
- Base: `transition-all duration-200`
- Selected: Gradient background + left border
- Hover: Lighter gradient + shadow

#### Button Hover
```vue
<button class="bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 hover:shadow-lg transition-all">
```

**Pattern**: Darker gradient + shadow on hover

### Tabs

#### Tab System with Colors

**Tab Configuration**:
```javascript
const stepTabs = [
  { label: 'Step 1: 需求', field: 'step1Requirements', color: 'blue' },
  { label: 'Step 2: 实体', field: 'step2Entities', color: 'purple' },
  { label: 'Step 3: API', field: 'step3Api', color: 'green' },
  { label: 'Step 4: 数据流', field: 'step4DataFlow', color: 'yellow' },
  { label: 'Step 5: 架构', field: 'step5Architecture', color: 'red' },
  { label: 'Step 6: 深入', field: 'step6DeepDive', color: 'indigo' },
  { label: '架构图', field: 'architectureDiagramUrl', color: 'pink' },
  { label: '要点总结', field: 'keyPoints', color: 'teal' }
]
```

**Color Mapping Function**:
```javascript
const getTabColorClass = (color, isActive) => {
  const colorMap = {
    blue: isActive
      ? 'text-blue-600 border-b-2 border-blue-600 bg-blue-50'
      : 'text-gray-600 hover:text-blue-600 hover:bg-blue-50',
    purple: isActive
      ? 'text-purple-600 border-b-2 border-purple-600 bg-purple-50'
      : 'text-gray-600 hover:text-purple-600 hover:bg-purple-50',
    // ... other colors
  }
  return colorMap[color] || (isActive ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-600')
}
```

**Tab Implementation**:
```vue
<div class="flex border-b border-gray-200">
  <button
    v-for="(step, index) in stepTabs"
    :key="index"
    @click="activeTab = index"
    :class="[
      'px-4 py-2 text-sm font-semibold transition-all duration-200',
      getTabColorClass(step.color, activeTab === index)
    ]"
  >
    {{ step.label }}
  </button>
</div>
```

**Active State**:
- Colored text: `text-{color}-600`
- Bottom border: `border-b-2 border-{color}-600`
- Light background: `bg-{color}-50`

**Inactive State**:
- Gray text: `text-gray-600`
- Hover: `hover:text-{color}-600 hover:bg-{color}-50`

---

## Code Examples

### Complete Card Example (Requirement Card)

```vue
<div class="bg-gradient-to-br from-blue-50 to-white rounded-xl border-2 border-blue-200 shadow-md hover:shadow-xl transition-all p-3">
  <!-- Card Header -->
  <div class="flex items-center gap-2 mb-2">
    <!-- Accent Bar -->
    <div class="w-2 h-6 bg-gradient-to-b from-blue-400 to-blue-600 rounded-full"></div>
    <!-- Title with Emoji -->
    <h4 class="text-sm font-bold text-blue-700">📋 Requirement</h4>
  </div>

  <!-- Card Content -->
  <div class="text-xs text-gray-700 whitespace-pre-wrap leading-relaxed">
    {{ content }}
  </div>
</div>
```

**Class Breakdown**:
- `bg-gradient-to-br`: Diagonal gradient (top-left to bottom-right)
- `from-blue-50 to-white`: Light blue to white gradient
- `rounded-xl`: Large border radius (0.75rem)
- `border-2 border-blue-200`: 2px colored border
- `shadow-md`: Medium shadow
- `hover:shadow-xl`: Extra large shadow on hover
- `transition-all`: Smooth transitions

### Complete Page Header Example

```vue
<div class="bg-gradient-to-r from-indigo-600 to-purple-600 border-b border-purple-700 px-6 py-4 flex-shrink-0 shadow-lg">
  <div class="flex justify-between items-center">
    <!-- Title + Badges -->
    <div class="flex items-center gap-3 flex-1">
      <h2 class="text-xl font-bold text-white">{{ title }}</h2>

      <!-- Difficulty Badge -->
      <span :class="[
        'px-3 py-1 text-xs rounded-full font-semibold shadow-md',
        difficulty === 'EASY' ? 'bg-gradient-to-r from-green-400 to-emerald-500 text-white' :
        difficulty === 'MEDIUM' ? 'bg-gradient-to-r from-yellow-400 to-orange-500 text-white' :
        'bg-gradient-to-r from-red-400 to-pink-500 text-white'
      ]">
        {{ difficultyText(difficulty) }} ({{ rating }}/10)
      </span>

      <!-- Official Badge -->
      <span v-if="isOfficial" class="px-3 py-1 text-xs rounded-full bg-white text-indigo-600 font-semibold shadow-md">
        ⭐ 官方案例
      </span>

      <!-- Company Tags -->
      <span
        v-for="tag in companyTags"
        :key="tag"
        class="px-3 py-1 text-xs rounded-full bg-white/90 text-indigo-700 font-medium shadow-sm"
      >
        🏢 {{ tag }}
      </span>
    </div>

    <!-- Action Button -->
    <button :class="[
      'px-4 py-2 rounded-lg transition-all text-sm font-bold shadow-md',
      isEditMode
        ? 'bg-gradient-to-r from-red-500 to-pink-500 text-white hover:from-red-600 hover:to-pink-600 hover:shadow-lg'
        : 'bg-gradient-to-r from-green-500 to-emerald-500 text-white hover:from-green-600 hover:to-emerald-600 hover:shadow-lg'
    ]">
      {{ isEditMode ? '✓ 退出编辑' : '✏️ 开始答题' }}
    </button>
  </div>
</div>
```

---

## Best Practices Checklist

When designing a new page:

- [ ] **Page Background**: Use subtle gradient (`from-{color}-50 via-{color}-50 to-{color}-50`)
- [ ] **Header**: Apply gradient background with white text
- [ ] **Subtitle**: Use lighter shade of header color (`text-{color}-100`)
- [ ] **Section Headers**: Use gradient text or colored text with emoji
- [ ] **Cards**: Add gradient backgrounds, colored borders, and shadows
- [ ] **Badges**: Use gradients for status indicators
- [ ] **Buttons**: Apply gradients with darker hover states
- [ ] **Tabs**: Assign unique colors, implement active/inactive states
- [ ] **Hover Effects**: Add subtle gradients and shadow enhancements
- [ ] **Transitions**: Use `transition-all duration-200` for smooth animations
- [ ] **Accessibility**: Ensure text contrast ratio ≥ 4.5:1
- [ ] **Consistency**: Use color families consistently across the page

---

## Design Anti-Patterns (Avoid These)

❌ **Flat, Monochromatic Design**:
```vue
<!-- Before: Too plain -->
<div class="bg-white border border-gray-200 p-4">
  <h3 class="text-gray-800">Section Title</h3>
</div>
```

✅ **Enhanced with Gradients**:
```vue
<!-- After: Visually appealing -->
<div class="bg-gradient-to-r from-indigo-500 to-purple-500 px-4 py-2 shadow-lg">
  <h3 class="text-white font-semibold">Section Title</h3>
</div>
```

❌ **Inconsistent Colors**:
```vue
<!-- Mixing random colors -->
<span class="bg-red-500">Badge 1</span>
<span class="bg-teal-600">Badge 2</span>
<span class="bg-yellow-400">Badge 3</span>
```

✅ **Consistent Color Family**:
```vue
<!-- Use gradients from same family -->
<span class="bg-gradient-to-r from-blue-400 to-purple-500">Badge 1</span>
<span class="bg-gradient-to-r from-purple-400 to-pink-500">Badge 2</span>
<span class="bg-gradient-to-r from-pink-400 to-red-500">Badge 3</span>
```

❌ **No Hover Feedback**:
```vue
<button class="bg-blue-600 text-white">Click Me</button>
```

✅ **With Hover Feedback**:
```vue
<button class="bg-gradient-to-r from-blue-500 to-purple-500 hover:from-blue-600 hover:to-purple-600 hover:shadow-lg transition-all text-white">
  Click Me
</button>
```

---

## Reference Implementation

**File**: `/frontend/src/views/SystemDesignCases.vue`

**Key Features Demonstrated**:
1. Three-column layout with gradients
2. Colorful card system (7 unique card types)
3. Multi-color tab system (8 colors)
4. Gradient text headers
5. Enhanced hover effects
6. Conditional button colors
7. Badge system with gradients

**Before/After Comparison**:
- **Before**: Plain white background, gray borders, no gradients
- **After**: Colorful gradients, enhanced shadows, visual hierarchy

**Lines to Study**:
- Page background: Line 2
- Header gradient: Lines 4-7
- Card system: Lines 200-260
- Tab colors: Lines 544-606
- Hover effects: Lines 22-27

---

**Document Version**: v1.0
**Created**: 2025-12-26
**Reference Page**: SystemDesignCases.vue (Phase 5.2)
**Maintainer**: Austin Xu
