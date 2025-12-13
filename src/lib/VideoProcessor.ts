/**
 * FFmpeg WebAssembly wrapper for video processing
 */

import { FFmpeg } from '@ffmpeg/ffmpeg'
import { fetchFile, toBlobURL } from '@ffmpeg/util'

export interface ProcessingOptions {
    startTime?: number
    endTime?: number
    aspectRatio?: { width: number; height: number }
    filterString?: string
    quality?: '4K' | '1080p' | '720p' | '480p'
    format?: 'mp4' | 'mov' | 'webm'
    fps?: number
}

export class VideoProcessor {
    private ffmpeg: FFmpeg
    private loaded = false

    constructor() {
        this.ffmpeg = new FFmpeg()
    }

    /**
     * Initialize FFmpeg with WebAssembly binaries
     */
    async load(onProgress?: (progress: number) => void): Promise<void> {
        if (this.loaded) return

        this.ffmpeg.on('log', ({ message }) => {
            console.log('FFmpeg:', message)
        })

        this.ffmpeg.on('progress', ({ progress }) => {
            if (onProgress) {
                onProgress(Math.round(progress * 100))
            }
        })

        console.log('Loading FFmpeg WebAssembly...')

        // Load FFmpeg core from CDN - using jsdelivr for better reliability
        try {
            const baseURL = 'https://unpkg.com/@ffmpeg/core-st@0.12.6/dist/esm'

            console.log('Loading from:', baseURL)

            await this.ffmpeg.load({
                coreURL: await toBlobURL(`${baseURL}/ffmpeg-core.js`, 'text/javascript'),
                wasmURL: await toBlobURL(`${baseURL}/ffmpeg-core.wasm`, 'application/wasm'),
            })

            this.loaded = true
            console.log('FFmpeg loaded successfully!')
        } catch (error) {
            console.error('Failed to load FFmpeg:', error)
            throw new Error('Failed to load video processing library. Please check your internet connection and try again.')
        }
    }

    /**
     * Process video with specified options
     */
    async processVideo(
        videoFile: File,
        options: ProcessingOptions,
        onProgress?: (progress: number) => void
    ): Promise<Blob> {
        if (!this.loaded) {
            await this.load(onProgress)
        }

        // Write input file to FFmpeg virtual filesystem
        const inputName = 'input.' + videoFile.name.split('.').pop()
        const outputName = `output.${options.format || 'mp4'}`

        await this.ffmpeg.writeFile(inputName, await fetchFile(videoFile))

        // Build FFmpeg command
        const ffmpegArgs = this.buildFFmpegCommand(inputName, outputName, options)

        console.log('FFmpeg command:', ffmpegArgs.join(' '))

        // Execute FFmpeg
        await this.ffmpeg.exec(ffmpegArgs)

        // Read output file
        const data = await this.ffmpeg.readFile(outputName)

        // Clean up
        await this.ffmpeg.deleteFile(inputName)
        await this.ffmpeg.deleteFile(outputName)

        // Convert to Blob
        const mimeType = this.getMimeType(options.format || 'mp4')
        return new Blob([data as BlobPart], { type: mimeType })
    }

    /**
     * Build FFmpeg command arguments
     */
    private buildFFmpegCommand(
        inputName: string,
        outputName: string,
        options: ProcessingOptions
    ): string[] {
        const args: string[] = ['-i', inputName]

        // Trim video
        if (options.startTime !== undefined) {
            args.push('-ss', options.startTime.toString())
        }
        if (options.endTime !== undefined && options.startTime !== undefined) {
            const duration = options.endTime - options.startTime
            args.push('-t', duration.toString())
        }

        // Video filters
        const filters: string[] = []

        // Aspect ratio / crop
        if (options.aspectRatio) {
            const { width, height } = options.aspectRatio
            // Scale to target aspect ratio while maintaining quality
            filters.push(`scale=${width * 180}:${height * 180}:force_original_aspect_ratio=decrease`)
            filters.push(`crop=${width * 180}:${height * 180}`)
        }

        // Color filters
        if (options.filterString) {
            filters.push(options.filterString)
        }

        if (filters.length > 0) {
            args.push('-vf', filters.join(','))
        }

        // Quality settings
        const qualitySettings = this.getQualitySettings(options.quality || '1080p')
        args.push(...qualitySettings)

        // Frame rate
        if (options.fps) {
            args.push('-r', options.fps.toString())
        }

        // Output format specific settings
        if (options.format === 'mp4') {
            args.push('-c:v', 'libx264', '-preset', 'medium', '-crf', '23')
            args.push('-c:a', 'aac', '-b:a', '128k')
        } else if (options.format === 'webm') {
            args.push('-c:v', 'libvpx-vp9', '-crf', '30')
            args.push('-c:a', 'libopus', '-b:a', '128k')
        }

        args.push(outputName)

        return args
    }

    /**
     * Get quality-specific FFmpeg settings
     */
    private getQualitySettings(quality: string): string[] {
        const settings: Record<string, string[]> = {
            '4K': ['-s', '3840x2160', '-b:v', '40M'],
            '1080p': ['-s', '1920x1080', '-b:v', '12M'],
            '720p': ['-s', '1280x720', '-b:v', '5M'],
            '480p': ['-s', '854x480', '-b:v', '2.5M'],
        }
        return settings[quality] || settings['1080p']
    }

    /**
     * Get MIME type for output format
     */
    private getMimeType(format: string): string {
        const mimeTypes: Record<string, string> = {
            mp4: 'video/mp4',
            mov: 'video/quicktime',
            webm: 'video/webm',
        }
        return mimeTypes[format] || 'video/mp4'
    }

    /**
     * Check if FFmpeg is loaded
     */
    isLoaded(): boolean {
        return this.loaded
    }
}

// Singleton instance
let processorInstance: VideoProcessor | null = null

export const getVideoProcessor = (): VideoProcessor => {
    if (!processorInstance) {
        processorInstance = new VideoProcessor()
    }
    return processorInstance
}
