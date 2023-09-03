/// <reference types="vite/client" />
interface ImportMetaEnv {
    readonly VITE_OAUTH_ISSUER: string
}

interface ImportMeta {
    readonly env: ImportMetaEnv
}