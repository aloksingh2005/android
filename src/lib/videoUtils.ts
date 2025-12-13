/**
 * Video processing utilities for metadata extraction and calculations
 */

export interface VideoMetadata {
    duration: number
    width: number
    height: number
    fps: number
    aspectRatio: string
}

export interface AspectRatio {
    ratio: string
    width: number
    height: number
    label: string
    platform: string
}

export const ASPECT_RATIOS: AspectRatio[] = [
    { ratio: '9:16', width: 9, height: 16, label: 'Story/Reels', platform: 'Instagram' },
    { ratio: '1:1', width: 1, height: 1, label: 'Feed Post', platform: 'Instagram' },
    { ratio: '4:5', width: 4, height: 5, label: 'Portrait', platform: 'Instagram' },
    { ratio: '16:9', width: 16, height: 9, label: 'YouTube', platform: 'YouTube' },
    { ratio: '4:3', width: 4, height: 3, label: 'Standard', platform: 'Classic' },
]

/**
 * Extract video metadata from a video element
 */
export const getVideoMetadata = (videoElement: HTMLVideoElement): VideoMetadata => {
    const width = videoElement.videoWidth
    const height = videoElement.videoHeight
    const duration = videoElement.duration

    // Calculate aspect ratio
    const gcd = (a: number, b: number): number => (b === 0 ? a : gcd(b, a % b))
    const divisor = gcd(width, height)
    const aspectRatio = `${width / divisor}:${height / divisor}`

    return {
        duration,
        width,
        height,
        fps: 30, // Default, would need metadata extraction for actual FPS
        aspectRatio,
    }
}

/**
 * Calculate crop dimensions for a target aspect ratio
 */
export const calculateCropDimensions = (
    sourceWidth: number,
    sourceHeight: number,
    targetRatio: AspectRatio
): { width: number; height: number; x: number; y: number } => {
    const sourceAspect = sourceWidth / sourceHeight
    const targetAspect = targetRatio.width / targetRatio.height

    let cropWidth: number
    let cropHeight: number

    if (sourceAspect > targetAspect) {
        // Source is wider, crop width
        cropHeight = sourceHeight
        cropWidth = cropHeight * targetAspect
    } else {
        // Source is taller, crop height
        cropWidth = sourceWidth
        cropHeight = cropWidth / targetAspect
    }

    // Center the crop
    const x = (sourceWidth - cropWidth) / 2
    const y = (sourceHeight - cropHeight) / 2

    return {
        width: Math.round(cropWidth),
        height: Math.round(cropHeight),
        x: Math.round(x),
        y: Math.round(y),
    }
}

/**
 * Estimate file size based on quality settings
 */
export const estimateFileSize = (
    durationSeconds: number,
    quality: '4K' | '1080p' | '720p' | '480p'
): string => {
    // Bitrate estimates in Mbps
    const bitrates = {
        '4K': 40,
        '1080p': 12,
        '720p': 5,
        '480p': 2.5,
    }

    const bitrate = bitrates[quality]
    const fileSizeMB = (durationSeconds * bitrate) / 8 // Convert to MB

    if (fileSizeMB > 1024) {
        return `${(fileSizeMB / 1024).toFixed(2)} GB`
    }
    return `${fileSizeMB.toFixed(0)} MB`
}

/**
 * Format seconds to MM:SS
 */
export const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

/**
 * Validate video file type
 */
export const isValidVideoFile = (file: File): boolean => {
    const validTypes = ['video/mp4', 'video/quicktime', 'video/webm', 'video/x-msvideo']
    return validTypes.includes(file.type)
}

/**
 * Generate waveform data from video audio
 * This is a simplified version - in production you'd use Web Audio API
 */
export const generateWaveformData = async (
    audioBuffer: AudioBuffer,
    samples: number = 100
): Promise<number[]> => {
    const rawData = audioBuffer.getChannelData(0)
    const blockSize = Math.floor(rawData.length / samples)
    const waveformData: number[] = []

    for (let i = 0; i < samples; i++) {
        const start = blockSize * i
        let sum = 0
        for (let j = 0; j < blockSize; j++) {
            sum += Math.abs(rawData[start + j])
        }
        waveformData.push(sum / blockSize)
    }

    // Normalize to 0-1
    const max = Math.max(...waveformData)
    return waveformData.map((val) => val / max)
}
