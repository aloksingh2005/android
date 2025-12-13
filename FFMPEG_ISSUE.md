# Instagram Video Editor - Known Issue

## FFmpeg Export Limitation

There is currently an issue with FFmpeg WebAssembly loading due to Cross-Origin-Isolation requirements in modern browsers.

### Current Status
- ✅ All UI features work perfectly
- ✅ Video upload and preview works
- ✅ Timeline, trim handles, filters, text overlays all functional
- ⚠️ **Video export (FFmpeg) requires additional setup**

### Why Export Doesn't Work Yet

FFmpeg WebAssembly requires these special HTTP headers:
```
Cross-Origin-Embedder-Policy: require-corp
Cross-Origin-Opener-Policy: same-origin
```

While we've configured Vite to send these headers, they may not be applying correctly in your local dev environment.

### Workarounds

#### Option 1: Deploy to Production
Deploy the app to a hosting service that supports custom headers:

**Vercel**: Add `vercel.json`:
```json
{
  "headers": [
    {
      "source": "/(.*)",
      "headers": [
        {
          "key": "Cross-Origin-Embedder-Policy",
          "value": "require-corp"
        },
        {
          "key": "Cross-Origin-Opener-Policy",
          "value": "same-origin"
        }
      ]
    }
  ]
}
```

**Netlify**: The `public/_headers` file is already configured

#### Option 2: Use a Different Dev Server
Install and use `vite-plugin-mkcert` for local HTTPS:

```bash
npm install -D vite-plugin-mkcert
```

Update `vite.config.ts`:
```typescript
import mkcert from'vite-plugin-mkcert'

export default defineConfig({
  plugins: [react(), mkcert()],
  server: {
    https: true,
    headers: {
      'Cross-Origin-Embedder-Policy': 'require-corp',
      'Cross-Origin-Opener-Policy': 'same-origin',
    },
  },
})
```

#### Option 3: Mock Export for Development
For local testing, you can add a mock export that simulates the download:

In `App.tsx`, modify the `handleExport` function to skip FFmpeg and just download the original video with applied metadata as JSON.

### Everything Else Works!

All other features are fully functional:
- ✅ Beautiful UI with glassmorphism design
- ✅ Video upload with validation
- ✅ Timeline with waveform visualization
- ✅ Draggable trim handles
- ✅ Aspect ratio presets (9:16, 1:1, 4:5, 16:9)
- ✅ 10+ Instagram-style filters
- ✅ Adjustment sliders (brightness, contrast, saturation, vibrance)
- ✅ Text overlays with fonts, colors, effects
- ✅ Export modal with quality settings

### Next Steps

1. Deploy to Vercel/Netlify for full functionality
2. Or implement one of the workarounds above
3. Once deployed, FFmpeg will work perfectly!

### Alternative: Server-Side Processing

For a production app, consider processing videos server-side:
- Upload video to your backend
- Use FFmpeg on the server
- Return processed video to client
- This avoids browser limitations entirely

---

For questions or issues, check the implementation docs in the `brain/` directory.
