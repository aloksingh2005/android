/**
 * Instagram-style filter definitions and FFmpeg command generators
 */

export interface Filter {
    id: string
    name: string
    description: string
    ffmpegFilter: string
}

export const FILTERS: Filter[] = [
    {
        id: 'none',
        name: 'None',
        description: 'Original video',
        ffmpegFilter: '',
    },
    {
        id: 'valencia',
        name: 'Valencia',
        description: 'Warm, faded vintage look',
        ffmpegFilter: 'eq=contrast=1.08:brightness=0.08:saturation=1.3,curves=all=0/0 0.5/0.58 1/1',
    },
    {
        id: 'clarendon',
        name: 'Clarendon',
        description: 'High contrast, vibrant',
        ffmpegFilter: 'eq=contrast=1.22:brightness=0.05:saturation=1.35',
    },
    {
        id: 'juno',
        name: 'Juno',
        description: 'Cool tones, brightened',
        ffmpegFilter: 'eq=contrast=1.15:brightness=0.12:saturation=0.9,colortemperature=6500',
    },
    {
        id: 'lark',
        name: 'Lark',
        description: 'Bright and desaturated',
        ffmpegFilter: 'eq=contrast=0.9:brightness=0.15:saturation=0.85',
    },
    {
        id: 'ludwig',
        name: 'Ludwig',
        description: 'Muted tones',
        ffmpegFilter: 'eq=contrast=1.05:brightness=0.05:saturation=0.88',
    },
    {
        id: 'aden',
        name: 'Aden',
        description: 'Cool, desaturated',
        ffmpegFilter: 'eq=contrast=0.9:brightness=0.1:saturation=0.85,hue=h=-10',
    },
    {
        id: 'perpetua',
        name: 'Perpetua',
        description: 'Soft, pastel',
        ffmpegFilter: 'eq=contrast=1.1:brightness=0.08:saturation=1.1',
    },
    {
        id: 'amaro',
        name: 'Amaro',
        description: 'Warm highlights',
        ffmpegFilter: 'eq=contrast=1.1:brightness=0.1:saturation=1.25',
    },
    {
        id: 'mayfair',
        name: 'Mayfair',
        description: 'Warm center, cool edge',
        ffmpegFilter: 'eq=contrast=1.08:brightness=0.08:saturation=1.1,colortemperature=5500',
    },
    {
        id: 'rise',
        name: 'Rise',
        description: 'Warm, soft glow',
        ffmpegFilter: 'eq=contrast=1.05:brightness=0.15:saturation=1.2,colortemperature=5000',
    },
]

export interface Adjustment {
    brightness: number // -100 to 100
    contrast: number // -100 to 100
    saturation: number // -100 to 100
    vibrance: number // -100 to 100
}

export const DEFAULT_ADJUSTMENT: Adjustment = {
    brightness: 0,
    contrast: 0,
    saturation: 0,
    vibrance: 0,
}

/**
 * Generate FFmpeg filter string from adjustments
 */
export const generateAdjustmentFilter = (adjustment: Adjustment): string => {
    const filters: string[] = []

    // Normalize values to FFmpeg ranges
    const brightness = adjustment.brightness / 100 // -1 to 1
    const contrast = 1 + adjustment.contrast / 100 // 0 to 2
    const saturation = 1 + adjustment.saturation / 100 // 0 to 2

    if (brightness !== 0 || contrast !== 1 || saturation !== 1) {
        filters.push(`eq=contrast=${contrast}:brightness=${brightness}:saturation=${saturation}`)
    }

    // Vibrance is similar to saturation but preserves skin tones better
    // We'll use a more sophisticated approach with color grading
    if (adjustment.vibrance !== 0) {
        const vibranceValue = 1 + adjustment.vibrance / 100
        filters.push(`vibrance=${vibranceValue}`)
    }

    return filters.join(',')
}

/**
 * Combine filter and adjustments into final FFmpeg filter string
 */
export const combineFilters = (filter: Filter, adjustment: Adjustment): string => {
    const filterParts: string[] = []

    if (filter.ffmpegFilter) {
        filterParts.push(filter.ffmpegFilter)
    }

    const adjustmentFilter = generateAdjustmentFilter(adjustment)
    if (adjustmentFilter) {
        filterParts.push(adjustmentFilter)
    }

    return filterParts.join(',')
}
