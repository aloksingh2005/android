import { useState } from 'react'

export interface TextOverlayData {
    id: string
    text: string
    font: string
    color: string
    position: 'top' | 'center' | 'bottom'
    size: number
    effects: {
        shadow: boolean
        outline: boolean
        background: boolean
    }
    animation: 'none' | 'fadeIn' | 'slide' | 'typewriter'
}

interface TextOverlayProps {
    overlays: TextOverlayData[]
    onAddOverlay: (overlay: TextOverlayData) => void
    onRemoveOverlay: (id: string) => void
}

const FONTS = ['Roboto', 'Inter', 'Montserrat', 'Playfair Display', 'Oswald', 'Merriweather']
const COLORS = [
    { name: 'White', value: '#FFFFFF' },
    { name: 'Pink', value: '#EC4899' },
    { name: 'Purple', value: '#A855F7' },
    { name: 'Yellow', value: '#FBBF24' },
    { name: 'Cyan', value: '#06B6D4' },
    { name: 'Green', value: '#10B981' },
]

export const TextOverlay = ({ overlays, onAddOverlay, onRemoveOverlay }: TextOverlayProps) => {
    const [text, setText] = useState('')
    const [font, setFont] = useState('Roboto')
    const [color, setColor] = useState('#FFFFFF')
    const [position, setPosition] = useState<'top' | 'center' | 'bottom'>('bottom')
    const [size, setSize] = useState(50)
    const [shadow, setShadow] = useState(true)
    const [outline, setOutline] = useState(false)
    const [background, setBackground] = useState(false)
    const [animation, setAnimation] = useState<'none' | 'fadeIn' | 'slide' | 'typewriter'>('fadeIn')

    const handleAddText = () => {
        if (!text.trim()) return

        const newOverlay: TextOverlayData = {
            id: Date.now().toString(),
            text,
            font,
            color,
            position,
            size,
            effects: { shadow, outline, background },
            animation,
        }

        onAddOverlay(newOverlay)
        setText('')
    }

    return (
        <div className="space-y-4">
            <h3 className="text-lg font-semibold flex items-center gap-2">
                <span>📝</span>
                Text Overlay
            </h3>

            {/* Text Input */}
            <div>
                <label className="block text-sm text-slate-400 mb-2">Your Text</label>
                <input
                    type="text"
                    value={text}
                    onChange={(e) => setText(e.target.value)}
                    placeholder="Enter your text here..."
                    className="w-full px-4 py-3 bg-slate-900/50 border-2 border-purple-500/30 rounded-xl text-white placeholder-slate-500 focus:border-purple-500 focus:outline-none"
                />
            </div>

            {/* Font Selector */}
            <div>
                <label className="block text-sm text-slate-400 mb-2">Font</label>
                <select
                    value={font}
                    onChange={(e) => setFont(e.target.value)}
                    className="w-full px-4 py-2 bg-slate-900/50 border border-white/10 rounded-lg text-white focus:border-purple-500 focus:outline-none"
                >
                    {FONTS.map((f) => (
                        <option key={f} value={f}>
                            {f}
                        </option>
                    ))}
                </select>
            </div>

            {/* Color Picker */}
            <div>
                <label className="block text-sm text-slate-400 mb-2">Color</label>
                <div className="flex gap-2">
                    {COLORS.map((c) => (
                        <button
                            key={c.value}
                            onClick={() => setColor(c.value)}
                            className={`w-10 h-10 rounded-lg border-2 transition-all ${color === c.value ? 'border-white scale-110' : 'border-white/20'
                                }`}
                            style={{ backgroundColor: c.value }}
                            title={c.name}
                        />
                    ))}
                </div>
            </div>

            {/* Position */}
            <div>
                <label className="block text-sm text-slate-400 mb-2">Position</label>
                <div className="grid grid-cols-3 gap-2">
                    {(['top', 'center', 'bottom'] as const).map((pos) => (
                        <button
                            key={pos}
                            onClick={() => setPosition(pos)}
                            className={`px-3 py-2 rounded-lg border-2 capitalize transition-all ${position === pos
                                    ? 'bg-purple-500/20 border-purple-500'
                                    : 'bg-slate-900/50 border-white/10 hover:border-purple-500/50'
                                }`}
                        >
                            {pos}
                        </button>
                    ))}
                </div>
            </div>

            {/* Size */}
            <div>
                <label className="block text-sm text-slate-400 mb-2">Size: {size}px</label>
                <input
                    type="range"
                    min="20"
                    max="100"
                    value={size}
                    onChange={(e) => setSize(parseInt(e.target.value))}
                    className="w-full h-2 bg-slate-800 rounded-lg appearance-none cursor-pointer accent-purple-500"
                />
            </div>

            {/* Effects */}
            <div>
                <label className="block text-sm text-slate-400 mb-2">Effects</label>
                <div className="space-y-2">
                    <label className="flex items-center gap-2 cursor-pointer">
                        <input
                            type="checkbox"
                            checked={shadow}
                            onChange={(e) => setShadow(e.target.checked)}
                            className="w-4 h-4 accent-purple-500"
                        />
                        <span className="text-sm">Shadow</span>
                    </label>
                    <label className="flex items-center gap-2 cursor-pointer">
                        <input
                            type="checkbox"
                            checked={outline}
                            onChange={(e) => setOutline(e.target.checked)}
                            className="w-4 h-4 accent-purple-500"
                        />
                        <span className="text-sm">Outline</span>
                    </label>
                    <label className="flex items-center gap-2 cursor-pointer">
                        <input
                            type="checkbox"
                            checked={background}
                            onChange={(e) => setBackground(e.target.checked)}
                            className="w-4 h-4 accent-purple-500"
                        />
                        <span className="text-sm">Background</span>
                    </label>
                </div>
            </div>

            {/* Animation */}
            <div>
                <label className="block text-sm text-slate-400 mb-2">Animation</label>
                <select
                    value={animation}
                    onChange={(e) => setAnimation(e.target.value as any)}
                    className="w-full px-4 py-2 bg-slate-900/50 border border-white/10 rounded-lg text-white focus:border-purple-500 focus:outline-none"
                >
                    <option value="none">None</option>
                    <option value="fadeIn">Fade In</option>
                    <option value="slide">Slide</option>
                    <option value="typewriter">Typewriter</option>
                </select>
            </div>

            {/* Add Button */}
            <button
                onClick={handleAddText}
                disabled={!text.trim()}
                className="w-full py-3 bg-gradient-to-r from-purple-600 to-pink-600 rounded-xl font-semibold disabled:opacity-50 disabled:cursor-not-allowed hover:shadow-lg hover:shadow-purple-500/50 transition-all"
            >
                ➕ Add Text
            </button>

            {/* Overlay List */}
            {overlays.length > 0 && (
                <div className="space-y-2">
                    <div className="text-sm font-medium text-slate-300">Added Text Overlays</div>
                    {overlays.map((overlay) => (
                        <div
                            key={overlay.id}
                            className="flex items-center justify-between bg-slate-900/50 border border-white/10 rounded-lg p-3"
                        >
                            <div className="flex-1">
                                <div className="font-medium" style={{ color: overlay.color }}>
                                    {overlay.text}
                                </div>
                                <div className="text-xs text-slate-400">
                                    {overlay.font} • {overlay.position} • {overlay.size}px
                                </div>
                            </div>
                            <button
                                onClick={() => onRemoveOverlay(overlay.id)}
                                className="px-3 py-1 text-sm bg-red-500/20 border border-red-500 rounded-lg hover:bg-red-500/30"
                            >
                                Remove
                            </button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    )
}
