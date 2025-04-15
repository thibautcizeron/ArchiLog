# FROM node:20 AS build
# WORKDIR /app
# COPY frontend/package*.json ./
# RUN npm install
# COPY /frontend ./
# CMD ["npm", "run", "dev", "--", "--host"]


FROM node:20 AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install --frozen-lockfile
RUN npm install npm-run-all --save-dev
COPY /frontend ./
RUN npm run build
FROM nginx:latest AS production
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
