import { useEffect, useRef, useState } from 'react'

interface TimelineProps {
    duration: number
    currentTime: number
    startTime: number
    endTime: number
    onSeek: (time: number) => void
    onTrimChange: (start: number, end: number) => void
    waveformData?: number[]
}

export const Timeline = ({
    duration,
    currentTime,
    startTime,
    endTime,
    onSeek,
    onTrimChange,
    waveformData,
}: TimelineProps) => {
    const timelineRef = useRef<HTMLDivElement>(null)
    const [isDraggingPlayhead, setIsDraggingPlayhead] = useState(false)
    const [isDraggingStart, setIsDraggingStart] = useState(false)
    const [isDraggingEnd, setIsDraggingEnd] = useState(false)

    const handleMouseDown = (type: 'playhead' | 'start' | 'end') => (e: React.MouseEvent) => {
        e.preventDefault()
        if (type === 'playhead') setIsDraggingPlayhead(true)
        else if (type === 'start') setIsDraggingStart(true)
        else setIsDraggingEnd(true)
    }

    const handleMouseMove = (e: MouseEvent) => {
        if (!timelineRef.current) return
        if (!isDraggingPlayhead && !isDraggingStart && !isDraggingEnd) return

        const rect = timelineRef.current.getBoundingClientRect()
        const x = Math.max(0, Math.min(e.clientX - rect.left, rect.width))
        const time = (x / rect.width) * duration

        if (isDraggingPlayhead) {
            onSeek(time)
        } else if (isDraggingStart) {
            onTrimChange(Math.min(time, endTime - 0.5), endTime)
        } else if (isDraggingEnd) {
            onTrimChange(startTime, Math.max(time, startTime + 0.5))
        }
    }

    const handleMouseUp = () => {
        setIsDraggingPlayhead(false)
        setIsDraggingStart(false)
        setIsDraggingEnd(false)
    }

    useEffect(() => {
        if (isDraggingPlayhead || isDraggingStart || isDraggingEnd) {
            window.addEventListener('mousemove', handleMouseMove)
            window.addEventListener('mouseup', handleMouseUp)
            return () => {
                window.removeEventListener('mousemove', handleMouseMove)
                window.removeEventListener('mouseup', handleMouseUp)
            }
        }
    }, [isDraggingPlayhead, isDraggingStart, isDraggingEnd, duration, startTime, endTime])

    const playheadPercent = (currentTime / duration) * 100
    const startPercent = (startTime / duration) * 100
    const endPercent = (endTime / duration) * 100

    return (
        <div className="w-full">
            <div
                ref={timelineRef}
                className="relative h-24 bg-slate-900/50 rounded-xl border border-white/10 cursor-pointer overflow-hidden"
                onClick={(e) => {
                    const rect = e.currentTarget.getBoundingClientRect()
                    const x = e.clientX - rect.left
                    const time = (x / rect.width) * duration
                    onSeek(time)
                }}
            >
                {/* Waveform */}
                {waveformData && (
                    <div className="absolute inset-0 flex items-center justify-around px-2">
                        {waveformData.map((value, i) => (
                            <div
                                key={i}
                                className="w-1 bg-gradient-to-t from-purple-500 to-pink-500 opacity-40 rounded-full"
                                style={{ height: `${value * 80}%` }}
                            />
                        ))}
                    </div>
                )}

                {/* Selected region highlight */}
                <div
                    className="absolute top-0 bottom-0 bg-purple-500/20 border-x-2 border-purple-500"
                    style={{
                        left: `${startPercent}%`,
                        right: `${100 - endPercent}%`,
                    }}
                />

                {/* Start trim handle */}
                <div
                    className="absolute top-0 bottom-0 w-1 bg-yellow-400 cursor-ew-resize hover:w-2 transition-all z-10"
                    style={{ left: `${startPercent}%` }}
                    onMouseDown={handleMouseDown('start')}
                >
                    <div className="absolute -left-2 top-1/2 -translate-y-1/2 w-4 h-8 bg-yellow-400 rounded-full border-2 border-white shadow-lg" />
                </div>

                {/* End trim handle */}
                <div
                    className="absolute top-0 bottom-0 w-1 bg-yellow-400 cursor-ew-resize hover:w-2 transition-all z-10"
                    style={{ left: `${endPercent}%` }}
                    onMouseDown={handleMouseDown('end')}
                >
                    <div className="absolute -left-2 top-1/2 -translate-y-1/2 w-4 h-8 bg-yellow-400 rounded-full border-2 border-white shadow-lg" />
                </div>

                {/* Playhead */}
                <div
                    className="absolute top-0 bottom-0 w-0.5 bg-white cursor-pointer z-20"
                    style={{ left: `${playheadPercent}%` }}
                    onMouseDown={handleMouseDown('playhead')}
                >
                    <div className="absolute -left-2 -top-1 w-4 h-4 bg-white rounded-full border-2 border-purple-500 shadow-lg" />
                </div>
            </div>

            {/* Time labels */}
            <div className="flex justify-between mt-2 text-sm text-slate-400">
                <span>{formatTime(startTime)}</span>
                <span>{formatTime(currentTime)}</span>
                <span>{formatTime(endTime)}</span>
            </div>
        </div>
    )
}

const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}
