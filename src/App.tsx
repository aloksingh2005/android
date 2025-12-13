import { useState } from 'react'
import './App.css'

function App() {
  const [videoFile, setVideoFile] = useState<File | null>(null)
  const [videoUrl, setVideoUrl] = useState<string>('')

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file && file.type.startsWith('video/')) {
      setVideoFile(file)
      setVideoUrl(URL.createObjectURL(file))
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
        {!videoFile ? (
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
          <div className="max-w-4xl mx-auto">
            <div className="bg-slate-800/50 backdrop-blur-lg rounded-2xl border border-white/10 p-6 mb-6">
              <video
                src={videoUrl}
                controls
                className="w-full rounded-xl bg-black"
              />
            </div>

            <div className="grid md:grid-cols-2 gap-6">
              <div className="bg-slate-800/50 backdrop-blur-lg rounded-2xl border border-white/10 p-6">
                <h3 className="text-lg font-semibold mb-4">✂️ Trim Video</h3>
                <p className="text-slate-400 text-sm">Coming soon...</p>
              </div>

              <div className="bg-slate-800/50 backdrop-blur-lg rounded-2xl border border-white/10 p-6">
                <h3 className="text-lg font-semibold mb-4">📐 Aspect Ratio</h3>
                <div className="grid grid-cols-2 gap-3">
                  <button className="px-4 py-3 bg-purple-500/10 border-2 border-purple-500 rounded-xl">
                    <div className="font-bold">9:16</div>
                    <div className="text-xs text-slate-400">Story/Reels</div>
                  </button>
                  <button className="px-4 py-3 bg-slate-900/50 border-2 border-white/10 rounded-xl hover:border-purple-500/50">
                    <div className="font-bold">1:1</div>
                    <div className="text-xs text-slate-400">Feed Post</div>
                  </button>
                  <button className="px-4 py-3 bg-slate-900/50 border-2 border-white/10 rounded-xl hover:border-purple-500/50">
                    <div className="font-bold">4:5</div>
                    <div className="text-xs text-slate-400">Portrait</div>
                  </button>
                  <button className="px-4 py-3 bg-slate-900/50 border-2 border-white/10 rounded-xl hover:border-purple-500/50">
                    <div className="font-bold">16:9</div>
                    <div className="text-xs text-slate-400">YouTube</div>
                  </button>
                </div>
              </div>
            </div>

            <div className="mt-6 flex gap-4">
              <button
                onClick={() => {
                  setVideoFile(null)
                  setVideoUrl('')
                }}
                className="px-6 py-3 bg-slate-800/50 border border-white/10 rounded-xl hover:bg-slate-700/50"
              >
                ← Start Over
              </button>
              <button className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-600 to-pink-600 rounded-xl font-semibold hover:shadow-lg hover:shadow-purple-500/50">
                ⬇️ Export Video (Coming Soon)
              </button>
            </div>
          </div>
        )}
      </main>
    </div>
  )
}

export default App
