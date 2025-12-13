/**
 * Custom hook for managing video editor state
 */

import { useState } from 'react'
import { FILTERS, DEFAULT_ADJUSTMENT, type Filter, type Adjustment } from '../lib/filters'
import { ASPECT_RATIOS, type AspectRatio } from '../lib/videoUtils'
import type { TextOverlayData } from '../components/TextOverlay'

export const useVideoEditor = () => {
    // Video file state
    const [videoFile, setVideoFile] = useState<File | null>(null)
    const [videoUrl, setVideoUrl] = useState<string>('')
    const [duration, setDuration] = useState<number>(0)
    const [currentTime, setCurrentTime] = useState<number>(0)

    // Trim state
    const [startTime, setStartTime] = useState<number>(0)
    const [endTime, setEndTime] = useState<number>(0)

    // Aspect ratio state
    const [selectedAspectRatio, setSelectedAspectRatio] = useState<AspectRatio>(ASPECT_RATIOS[0])

    // Filter state
    const [selectedFilter, setSelectedFilter] = useState<Filter>(FILTERS[0])
    const [adjustment, setAdjustment] = useState<Adjustment>(DEFAULT_ADJUSTMENT)

    // Text overlay state
    const [textOverlays, setTextOverlays] = useState<TextOverlayData[]>([])

    // Tab state
    const [activeTab, setActiveTab] = useState<'trim' | 'aspect' | 'filters' | 'text'>('trim')

    // Video element ref (set externally)
    const [videoElement, setVideoElement] = useState<HTMLVideoElement | null>(null)

    const loadVideo = (file: File) => {
        setVideoFile(file)
        const url = URL.createObjectURL(file)
        setVideoUrl(url)
    }

    const handleVideoMetadataLoaded = (video: HTMLVideoElement) => {
        setVideoElement(video)
        setDuration(video.duration)
        setStartTime(0)
        setEndTime(video.duration)
    }

    const handleTrimChange = (start: number, end: number) => {
        setStartTime(start)
        setEndTime(end)
    }

    const handleSeek = (time: number) => {
        setCurrentTime(time)
        if (videoElement) {
            videoElement.currentTime = time
        }
    }

    const addTextOverlay = (overlay: TextOverlayData) => {
        setTextOverlays([...textOverlays, overlay])
    }

    const removeTextOverlay = (id: string) => {
        setTextOverlays(textOverlays.filter((o) => o.id !== id))
    }

    const reset = () => {
        if (videoUrl) {
            URL.revokeObjectURL(videoUrl)
        }
        setVideoFile(null)
        setVideoUrl('')
        setDuration(0)
        setCurrentTime(0)
        setStartTime(0)
        setEndTime(0)
        setSelectedAspectRatio(ASPECT_RATIOS[0])
        setSelectedFilter(FILTERS[0])
        setAdjustment(DEFAULT_ADJUSTMENT)
        setTextOverlays([])
        setActiveTab('trim')
    }

    return {
        // State
        videoFile,
        videoUrl,
        duration,
        currentTime,
        startTime,
        endTime,
        selectedAspectRatio,
        selectedFilter,
        adjustment,
        textOverlays,
        activeTab,
        videoElement,

        // Actions
        loadVideo,
        handleVideoMetadataLoaded,
        handleTrimChange,
        handleSeek,
        setSelectedAspectRatio,
        setSelectedFilter,
        setAdjustment,
        addTextOverlay,
        removeTextOverlay,
        setActiveTab,
        reset,
    }
}
