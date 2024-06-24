FROM node:lts-alpine
RUN npm install --legacy-peer-deps -g http-server
WORKDIR /app
COPY package*.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build
EXPOSE 3000
CMD [ "http-server", "dist" ]