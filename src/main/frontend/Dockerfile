# ─── Stage 1: Build Angular ───────────────────────────────────
FROM node:20-alpine AS builder
WORKDIR /app

# Install deps first (layer cache)
COPY package.json package-lock.json ./
RUN npm ci --silent

# Build production bundle (targets /api via nginx proxy)
COPY . .
RUN npm run build -- --configuration production

# ─── Stage 2: Serve with nginx ────────────────────────────────
FROM nginx:1.27-alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx static content
RUN rm -rf ./*

# Copy built Angular app
COPY --from=builder /app/dist/enicar-connect/browser ./

# nginx config: SPA routing + API proxy
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
