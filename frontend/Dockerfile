# Use Node.js 22 Alpine as the base image
FROM node:22-alpine AS build

# Set working directory
WORKDIR /app

# Copy package.json and install dependencies
COPY package.json package-lock.json ./
RUN npm ci

# Ensure Angular CLI is globally installed and executable
RUN npm install -g @angular/cli
RUN chmod +x /app/node_modules/.bin/ng  # Fix permission issue

# Copy application source
COPY . .

# Run Angular build
RUN npm run build --configuration=docker

# Use nginx to serve the built frontend
FROM nginx:alpine AS final

COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
