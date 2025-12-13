import { useState } from 'react'

interface ExportModalProps {
    isOpen: boolean
    onClose: () => void
    onExport: (settings: ExportSettings) => void
    isExporting: boolean
    progress: number
    duration: number
}

export interface ExportSettings {
    format: 'mp4' | 'mov' | 'webm'
    quality: '4K' | '1080p' | '720p' | '480p'
    fps: 30 | 60
}

import { estimateFileSize } from '../lib/videoUtils'

export const ExportModal = ({
    isOpen,
    onClose,
    onExport,
    isExporting,
    progress,
    duration,
}: ExportModalProps) => {
    const [format, setFormat] = useState<'mp4' | 'mov' | 'webm'>('mp4')
    const [quality, setQuality] = useState<'4K' | '1080p' | '720p' | '480p'>('1080p')
    const [fps, setFps] = useState<30 | 60>(30)

    if (!isOpen) return null

    const fileSize = estimateFileSize(duration, quality)

    const handleExport = () => {
        onExport({ format, quality, fps })
    }

    return (
        <div className="fixed inset-0 bg-black/80 backdrop-blur-sm flex items-center justify-center z-50 p-4">
            <div className="bg-slate-800 border border-white/10 rounded-2xl p-6 max-w-md w-full">
                {!isExporting ? (
                    <>
                        <h2 className="text-2xl font-bold mb-2">Export Video</h2>
                        <p className="text-slate-400 text-sm mb-6">
                            Configure your export settings below
                        </p>

                        {/* Format */}
                        <div className="mb-4">
                            <label className="block text-sm font-medium mb-2">Format</label>
                            <select
                                value={format}
                                onChange={(e) => setFormat(e.target.value as any)}
                                className="w-full px-4 py-2 bg-slate-900 border border-white/10 rounded-lg text-white focus:border-purple-500 focus:outline-none"
                            >
                                <option value="mp4">MP4 (H.264)</option>
                                <option value="mov">MOV (QuickTime)</option>
                                <option value="webm">WebM (VP9)</option>
                            </select>
                        </div>

                        {/* Quality */}
                        <div className="mb-4">
                            <label className="block text-sm font-medium mb-2">Quality</label>
                            <div className="grid grid-cols-2 gap-2">
                                {(['4K', '1080p', '720p', '480p'] as const).map((q) => (
                                    <button
                                        key={q}
                                        onClick={() => setQuality(q)}
                                        className={`px-4 py-2 rounded-lg border-2 transition-all ${quality === q
                                                ? 'bg-purple-500/20 border-purple-500'
                                                : 'bg-slate-900 border-white/10 hover:border-purple-500/50'
                                            }`}
                                    >
                                        <div className="font-semibold">{q}</div>
                                        <div className="text-xs text-slate-400">
                                            {estimateFileSize(duration, q)}
                                        </div>
                                    </button>
                                ))}
                            </div>
                        </div>

                        {/* FPS */}
                        <div className="mb-6">
                            <label className="block text-sm font-medium mb-2">Frame Rate</label>
                            <div className="grid grid-cols-2 gap-2">
                                <button
                                    onClick={() => setFps(30)}
                                    className={`px-4 py-2 rounded-lg border-2 transition-all ${fps === 30
                                            ? 'bg-purple-500/20 border-purple-500'
                                            : 'bg-slate-900 border-white/10 hover:border-purple-500/50'
                                        }`}
                                >
                                    30 FPS
                                </button>
                                <button
                                    onClick={() => setFps(60)}
                                    className={`px-4 py-2 rounded-lg border-2 transition-all ${fps === 60
                                            ? 'bg-purple-500/20 border-purple-500'
                                            : 'bg-slate-900 border-white/10 hover:border-purple-500/50'
                                        }`}
                                >
                                    60 FPS
                                </button>
                            </div>
                        </div>

                        {/* Estimated file size */}
                        <div className="bg-slate-900/50 rounded-lg p-3 mb-6 border border-white/10">
                            <div className="text-sm text-slate-400">Estimated file size</div>
                            <div className="text-xl font-bold text-purple-400">{fileSize}</div>
                        </div>

                        {/* Buttons */}
                        <div className="flex gap-3">
                            <button
                                onClick={onClose}
                                className="flex-1 py-3 bg-slate-700 rounded-xl hover:bg-slate-600 transition-all"
                            >
                                Cancel
                            </button>
                            <button
                                onClick={handleExport}
                                className="flex-1 py-3 bg-gradient-to-r from-purple-600 to-pink-600 rounded-xl font-semibold hover:shadow-lg hover:shadow-purple-500/50 transition-all"
                            >
                                ⬇️ Export
                            </button>
                        </div>
                    </>
                ) : (
                    <>
                        <div className="text-center mb-6">
                            <div className="text-6xl mb-4">🎬</div>
                            <h2 className="text-2xl font-bold mb-2">Exporting Video</h2>
                            <p className="text-slate-400">Please wait while we process your video...</p>
                        </div>

                        {/* Progress Bar */}
                        <div className="mb-4">
                            <div className="flex justify-between text-sm mb-2">
                                <span className="text-slate-400">Progress</span>
                                <span className="font-semibold">{progress}%</span>
                            </div>
                            <div className="h-3 bg-slate-900 rounded-full overflow-hidden">
                                <div
                                    className="h-full bg-gradient-to-r from-purple-600 to-pink-600 transition-all duration-300"
                                    style={{ width: `${progress}%` }}
                                />
                            </div>
                        </div>

                        <div className="text-center text-sm text-slate-400">
                            This may take a few moments...
                        </div>
                    </>
                )}
            </div>
        </div>
    )
}
