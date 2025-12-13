import { ASPECT_RATIOS, type AspectRatio } from '../lib/videoUtils'

interface AspectRatioSelectorProps {
    selectedRatio: AspectRatio
    onSelect: (ratio: AspectRatio) => void
}

export const AspectRatioSelector = ({ selectedRatio, onSelect }: AspectRatioSelectorProps) => {
    return (
        <div className="space-y-4">
            <h3 className="text-lg font-semibold flex items-center gap-2">
                <span>📐</span>
                Aspect Ratio
            </h3>

            <div className="grid grid-cols-2 gap-3">
                {ASPECT_RATIOS.map((ratio) => {
                    const isSelected = ratio.ratio === selectedRatio.ratio

                    return (
                        <button
                            key={ratio.ratio}
                            onClick={() => onSelect(ratio)}
                            className={`px-4 py-3 rounded-xl border-2 transition-all ${isSelected
                                    ? 'bg-purple-500/10 border-purple-500 shadow-lg shadow-purple-500/30'
                                    : 'bg-slate-900/50 border-white/10 hover:border-purple-500/50'
                                }`}
                        >
                            <div className="font-bold text-lg">{ratio.ratio}</div>
                            <div className="text-xs text-slate-400">{ratio.label}</div>
                            <div className="text-xs text-purple-400">{ratio.platform}</div>
                        </button>
                    )
                })}
            </div>

            <div className="bg-slate-900/50 rounded-xl p-4 border border-white/10">
                <div className="text-sm text-slate-400 mb-2">Preview:</div>
                <div className="flex justify-center items-center h-32">
                    <div
                        className="border-2 border-purple-500 bg-purple-500/10 flex items-center justify-center text-xs text-slate-400"
                        style={{
                            width: `${selectedRatio.width * 12}px`,
                            height: `${selectedRatio.height * 12}px`,
                            maxWidth: '100%',
                            maxHeight: '100%',
                        }}
                    >
                        {selectedRatio.ratio}
                    </div>
                </div>
            </div>

            <div className="text-xs text-slate-500">
                <strong>Platform Guide:</strong>
                <ul className="mt-2 space-y-1">
                    <li>• 9:16 - Instagram Stories, Reels, TikTok</li>
                    <li>• 1:1 - Instagram Feed (Square)</li>
                    <li>• 4:5 - Instagram Feed (Portrait)</li>
                    <li>• 16:9 - YouTube, Landscape</li>
                </ul>
            </div>
        </div>
    )
}
