# SvelteKit Application

This is a [SvelteKit](https://kit.svelte.dev/) application that has been migrated from Sapper.

## Getting started

### Running the project

You can install dependencies and run the project in development mode with:

```bash
npm install # or yarn
npm run dev
```

Open up [localhost:5173](http://localhost:5173) and start clicking around.

Consult [kit.svelte.dev](https://kit.svelte.dev) for help getting started.

## Building

To create a production version of your app:

```bash
npm run build
```

You can preview the production build with `npm run preview`.

## Structure

SvelteKit expects to find two directories in the root of your project — `src` and `static`.

### src

The [src](src) directory contains the entry points for your app — `app.html`, and a `routes` directory.

#### src/routes

This is the heart of your SvelteKit app. There are two kinds of routes — *pages*, and *server routes*.

**Pages** are Svelte components written in `.svelte` files. When a user first visits the application, they will be served a server-rendered version of the route in question, plus some JavaScript that 'hydrates' the page and initialises a client-side router. From that point forward, navigating to other pages is handled entirely on the client for a fast, app-like feel.

**Server routes** are modules written in `.js` files, that export functions corresponding to HTTP methods. Each function receives a `RequestEvent` object as its argument. This is useful for creating a JSON API, for example.

There are three simple rules for naming the files that define your routes:

* A file called `src/routes/about/+page.svelte` corresponds to the `/about` route. A file called `src/routes/blog/[slug]/+page.svelte` corresponds to the `/blog/:slug` route, in which case `params.slug` is available to the route
* The file `src/routes/+page.svelte` corresponds to the root of your app. `src/routes/about/+page.svelte` is treated the same as `src/routes/about/index.svelte`.
* Files and directories with a leading underscore do *not* create routes. This allows you to colocate helper modules and components with the routes that depend on them — for example you could have a file called `src/routes/_helpers/datetime.js` and it would *not* create a `/_helpers/datetime` route

### static

The [static](static) directory contains any static assets that should be available. These are served using [sirv](https://github.com/lukeed/sirv).

## Deployment

This application is built as a static site and integrated with a Java backend via Maven. The build process:

1. Runs `npm run build` to create the static site
2. Copies the generated files from `.svelte-kit/output/client/` to the Maven target directory
3. The Java backend serves these static files

## Migration from Sapper

This application has been migrated from Sapper to SvelteKit. Key changes include:

- Build command changed from `npx sapper export` to `npm run build`
- Output directory changed from `__sapper__/export/` to `.svelte-kit/output/client/`
- Updated routing conventions to use SvelteKit's file-based routing
- Updated configuration files and build scripts