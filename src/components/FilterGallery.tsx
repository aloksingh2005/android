import { FILTERS, type Filter, type Adjustment, DEFAULT_ADJUSTMENT } from '../lib/filters'
import { useState } from 'react'

interface FilterGalleryProps {
    selectedFilter: Filter
    adjustment: Adjustment
    onFilterSelect: (filter: Filter) => void
    onAdjustmentChange: (adjustment: Adjustment) => void
}

export const FilterGallery = ({
    selectedFilter,
    adjustment,
    onFilterSelect,
    onAdjustmentChange,
}: FilterGalleryProps) => {
    const [localAdjustment, setLocalAdjustment] = useState(adjustment)

    const handleAdjustmentChange = (key: keyof Adjustment, value: number) => {
        const newAdjustment = { ...localAdjustment, [key]: value }
        setLocalAdjustment(newAdjustment)
        onAdjustmentChange(newAdjustment)
    }

    const handleReset = () => {
        setLocalAdjustment(DEFAULT_ADJUSTMENT)
        onAdjustmentChange(DEFAULT_ADJUSTMENT)
        onFilterSelect(FILTERS[0]) // Reset to 'None' filter
    }

    return (
        <div className="space-y-4">
            <div className="flex items-center justify-between">
                <h3 className="text-lg font-semibold flex items-center gap-2">
                    <span>🎨</span>
                    Filters & Effects
                </h3>
                <button
                    onClick={handleReset}
                    className="px-3 py-1 text-sm bg-slate-800/50 border border-white/10 rounded-lg hover:bg-slate-700/50"
                >
                    Reset All
                </button>
            </div>

            {/* Filter Gallery */}
            <div className="overflow-x-auto">
                <div className="flex gap-3 pb-2">
                    {FILTERS.map((filter) => {
                        const isSelected = filter.id === selectedFilter.id

                        return (
                            <button
                                key={filter.id}
                                onClick={() => onFilterSelect(filter)}
                                className={`flex-shrink-0 group transition-all ${isSelected ? 'scale-105' : 'hover:scale-105'
                                    }`}
                            >
                                <div
                                    className={`w-20 h-20 rounded-lg border-2 bg-gradient-to-br from-purple-500/20 to-pink-500/20 flex items-center justify-center transition-all ${isSelected
                                            ? 'border-purple-500 shadow-lg shadow-purple-500/30'
                                            : 'border-white/10 group-hover:border-purple-500/50'
                                        }`}
                                >
                                    <span className="text-2xl">
                                        {filter.id === 'none' ? '✨' : '🎨'}
                                    </span>
                                </div>
                                <div className="text-xs text-center mt-1 text-slate-400">
                                    {filter.name}
                                </div>
                            </button>
                        )
                    })}
                </div>
            </div>

            {/* Filter Description */}
            <div className="bg-slate-900/50 rounded-lg p-3 border border-white/10">
                <div className="text-sm font-medium">{selectedFilter.name}</div>
                <div className="text-xs text-slate-400">{selectedFilter.description}</div>
            </div>

            {/* Adjustment Sliders */}
            <div className="space-y-3">
                <div className="text-sm font-medium text-slate-300">Adjustments</div>

                {/* Brightness */}
                <div>
                    <div className="flex justify-between text-xs text-slate-400 mb-1">
                        <span>🌞 Brightness</span>
                        <span>{localAdjustment.brightness}%</span>
                    </div>
                    <input
                        type="range"
                        min="-100"
                        max="100"
                        value={localAdjustment.brightness}
                        onChange={(e) => handleAdjustmentChange('brightness', parseInt(e.target.value))}
                        className="w-full h-2 bg-slate-800 rounded-lg appearance-none cursor-pointer accent-purple-500"
                    />
                </div>

                {/* Contrast */}
                <div>
                    <div className="flex justify-between text-xs text-slate-400 mb-1">
                        <span>⚖️ Contrast</span>
                        <span>{localAdjustment.contrast}%</span>
                    </div>
                    <input
                        type="range"
                        min="-100"
                        max="100"
                        value={localAdjustment.contrast}
                        onChange={(e) => handleAdjustmentChange('contrast', parseInt(e.target.value))}
                        className="w-full h-2 bg-slate-800 rounded-lg appearance-none cursor-pointer accent-purple-500"
                    />
                </div>

                {/* Saturation */}
                <div>
                    <div className="flex justify-between text-xs text-slate-400 mb-1">
                        <span>🎨 Saturation</span>
                        <span>{localAdjustment.saturation}%</span>
                    </div>
                    <input
                        type="range"
                        min="-100"
                        max="100"
                        value={localAdjustment.saturation}
                        onChange={(e) => handleAdjustmentChange('saturation', parseInt(e.target.value))}
                        className="w-full h-2 bg-slate-800 rounded-lg appearance-none cursor-pointer accent-purple-500"
                    />
                </div>

                {/* Vibrance */}
                <div>
                    <div className="flex justify-between text-xs text-slate-400 mb-1">
                        <span>✨ Vibrance</span>
                        <span>{localAdjustment.vibrance}%</span>
                    </div>
                    <input
                        type="range"
                        min="-100"
                        max="100"
                        value={localAdjustment.vibrance}
                        onChange={(e) => handleAdjustmentChange('vibrance', parseInt(e.target.value))}
                        className="w-full h-2 bg-slate-800 rounded-lg appearance-none cursor-pointer accent-purple-500"
                    />
                </div>
            </div>
        </div>
    )
}
