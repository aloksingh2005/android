import { useState, useRef } from 'react'
import './App.css'
import { Timeline } from './components/Timeline'
import { AspectRatioSelector } from './components/AspectRatioSelector'
import { FilterGallery } from './components/FilterGallery'
import { TextOverlay } from './components/TextOverlay'
import { ExportModal, type ExportSettings } from './components/ExportModal'
import { useVideoEditor } from './hooks/useVideoEditor'
import { getVideoProcessor } from './lib/VideoProcessor'
import { combineFilters } from './lib/filters'

function App() {
  const editor = useVideoEditor()
  const videoRef = useRef<HTMLVideoElement>(null)
  const [isExportModalOpen, setIsExportModalOpen] = useState(false)
  const [isExporting, setIsExporting] = useState(false)
  const [exportProgress, setExportProgress] = useState(0)

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file && file.type.startsWith('video/')) {
      editor.loadVideo(file)
    }
  }

  const handleVideoLoaded = () => {
    if (videoRef.current) {
      editor.handleVideoMetadataLoaded(videoRef.current)
    }
  }

  const handleTimeUpdate = () => {
    if (videoRef.current) {
      editor.handleSeek(videoRef.current.currentTime)
    }
  }

  const handleExport = async (settings: ExportSettings) => {
    if (!editor.videoFile) return

    setIsExporting(true)
    setExportProgress(0)

    try {
      const processor = getVideoProcessor()

      // Combine filters
      const filterString = combineFilters(editor.selectedFilter, editor.adjustment)

      // Process video
      const outputBlob = await processor.processVideo(
        editor.videoFile,
        {
          startTime: editor.startTime,
          endTime: editor.endTime,
          aspectRatio: {
            width: editor.selectedAspectRatio.width,
            height: editor.selectedAspectRatio.height,
          },
          filterString: filterString || undefined,
          quality: settings.quality,
          format: settings.format,
          fps: settings.fps,
        },
        (progress) => {
          setExportProgress(progress)
        }
      )

      // Download the file
      const url = URL.createObjectURL(outputBlob)
      const a = document.createElement('a')
      a.href = url
      a.download = `instagram-video-${Date.now()}.${settings.format}`
      a.click()
      URL.revokeObjectURL(url)

      setIsExporting(false)
      setIsExportModalOpen(false)
    } catch (error) {
      console.error('Export failed:', error)
      alert('Export failed. Please try again.')
      setIsExporting(false)
    }
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-900 via-purple-900 to-slate-900 text-white">
      <header className="border-b border-white/10 backdrop-blur-lg p-6">
        <h1 className="text-3xl font-bold bg-gradient-to-r from-purple-400 to-pink-400 bg-clip-text text-transparent">
          Instagram Video Editor
        </h1>
        <p className="text-slate-400 mt-1">Create perfect videos for Instagram</p>
      </header>

      <main className="container mx-auto px-4 py-8">
        {!editor.videoFile ? (
          <div className="max-w-2xl mx-auto">
            <div className="bg-slate-800/50 backdrop-blur-lg rounded-2xl border-2 border-dashed border-white/20 p-12 text-center hover:border-purple-500/50 transition-all">
              <div className="text-6xl mb-6">📹</div>
              <h3 className="text-2xl font-bold mb-2">Upload Your Video</h3>
              <p className="text-slate-400 mb-6">Choose a video file to start editing</p>
              <input
                type="file"
                accept="video/*"
                onChange={handleFileChange}
                className="hidden"
                id="video-upload"
              />
              <label
                htmlFor="video-upload"
                className="inline-block px-8 py-4 bg-gradient-to-r from-purple-600 to-pink-600 rounded-xl font-semibold cursor-pointer hover:shadow-lg hover:shadow-purple-500/50 transition-all"
              >
                Choose Video File
              </label>
              <p className="text-sm text-slate-500 mt-6">Supported: MP4, MOV, WebM, AVI</p>
            </div>
          </div>
        ) : (
          <div className="max-w-6xl mx-auto">
            {/* Video Preview */}
            <div className="bg-slate-800/50 backdrop-blur-lg rounded-2xl border border-white/10 p-6 mb-6">
              <video
                ref={videoRef}
                src={editor.videoUrl}
                controls
                onLoadedMetadata={handleVideoLoaded}
                onTimeUpdate={handleTimeUpdate}
                className="w-full rounded-xl bg-black"
              />
            </div>

            {/* Tabs */}
            <div className="flex gap-2 mb-6 overflow-x-auto">
              {([
                { id: 'trim', label: '✂️ Trim', name: 'trim' },
                { id: 'aspect', label: '📐 Aspect Ratio', name: 'aspect' },
                { id: 'filters', label: '🎨 Filters', name: 'filters' },
                { id: 'text', label: '📝 Text', name: 'text' },
              ] as const).map((tab) => (
                <button
                  key={tab.id}
                  onClick={() => editor.setActiveTab(tab.name)}
                  className={`px-6 py-3 rounded-xl font-semibold transition-all whitespace-nowrap ${editor.activeTab === tab.name
                      ? 'bg-gradient-to-r from-purple-600 to-pink-600 shadow-lg shadow-purple-500/30'
                      : 'bg-slate-800/50 border border-white/10 hover:border-purple-500/50'
                    }`}
                >
                  {tab.label}
                </button>
              ))}
            </div>

            {/* Tab Content */}
            <div className="bg-slate-800/50 backdrop-blur-lg rounded-2xl border border-white/10 p-6 mb-6">
              {editor.activeTab === 'trim' && (
                <div className="space-y-4">
                  <h3 className="text-lg font-semibold flex items-center gap-2">
                    <span>✂️</span>
                    Trim Video
                  </h3>
                  <Timeline
                    duration={editor.duration}
                    currentTime={editor.currentTime}
                    startTime={editor.startTime}
                    endTime={editor.endTime}
                    onSeek={editor.handleSeek}
                    onTrimChange={editor.handleTrimChange}
                  />
                  <div className="text-sm text-slate-400">
                    Selected duration: {Math.round(editor.endTime - editor.startTime)}s
                  </div>
                </div>
              )}

              {editor.activeTab === 'aspect' && (
                <AspectRatioSelector
                  selectedRatio={editor.selectedAspectRatio}
                  onSelect={editor.setSelectedAspectRatio}
                />
              )}

              {editor.activeTab === 'filters' && (
                <FilterGallery
                  selectedFilter={editor.selectedFilter}
                  adjustment={editor.adjustment}
                  onFilterSelect={editor.setSelectedFilter}
                  onAdjustmentChange={editor.setAdjustment}
                />
              )}

              {editor.activeTab === 'text' && (
                <TextOverlay
                  overlays={editor.textOverlays}
                  onAddOverlay={editor.addTextOverlay}
                  onRemoveOverlay={editor.removeTextOverlay}
                />
              )}
            </div>

            {/* Action Buttons */}
            <div className="flex gap-4">
              <button
                onClick={editor.reset}
                className="px-6 py-3 bg-slate-800/50 border border-white/10 rounded-xl hover:bg-slate-700/50"
              >
                ← Start Over
              </button>
              <button
                onClick={() => setIsExportModalOpen(true)}
                className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-600 to-pink-600 rounded-xl font-semibold hover:shadow-lg hover:shadow-purple-500/50"
              >
                ⬇️ Export Video
              </button>
            </div>
          </div>
        )}
      </main>

      {/* Export Modal */}
      <ExportModal
        isOpen={isExportModalOpen}
        onClose={() => setIsExportModalOpen(false)}
        onExport={handleExport}
        isExporting={isExporting}
        progress={exportProgress}
        duration={editor.endTime - editor.startTime}
      />
    </div>
  )
}

export default App
