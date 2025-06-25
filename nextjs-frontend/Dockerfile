# Use Node.js as the base image
FROM node:20-alpine AS base

# Set working directory
WORKDIR /app

# Install dependencies
FROM base AS deps
COPY package.json package-lock.json* ./
RUN npm ci

# Build the app
FROM deps AS builder
COPY . .
# This will use the NEXT_PUBLIC_* environment variables at build time
RUN npm run build

# Production image
FROM base AS runner
ENV NODE_ENV=production

# Create a non-root user
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs
USER nextjs

# Copy built app
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static
COPY --from=builder --chown=nextjs:nodejs /app/public ./public

# Expose port
EXPOSE 3000

# Set environment variables that can be overridden at runtime
ENV PORT=3000
ENV HOSTNAME=0.0.0.0

# Start the app
CMD ["node", "server.js"]