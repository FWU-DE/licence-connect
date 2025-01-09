# Base image
FROM node:22

# Create app directory
WORKDIR /usr/src

# A wildcard is used to ensure both package.json AND package-lock.json are copied
COPY ./licence-connect/package.json ./
COPY ./yarn.lock ./

# Bundle app source
COPY ./licence-connect .

# Install app dependencies
RUN yarn install

# Creates a "dist" folder with the production build
RUN yarn build

# Start the server using the production build
CMD [ "node", "dist/main.js" ]