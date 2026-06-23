# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Copiar arquivos de dependências
COPY package*.json ./
RUN npm ci

# Copiar o restante da aplicação e realizar o build
COPY . .
RUN npm run build --configuration=production

# Serve stage
FROM nginx:alpine
# Copiar configuração customizada do NGINX
COPY nginx.conf /etc/nginx/conf.d/default.conf

# IMPORTANTE: Verifique se o caminho do build bate com o seu Angular (v17+ costuma ser dist/frontend/browser)
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
