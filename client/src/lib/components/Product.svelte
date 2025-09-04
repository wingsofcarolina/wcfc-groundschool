<script>
  import { onMount } from 'svelte'
  import Section from "$lib/components/Section.svelte";

  export let title;
  export let publisher = null;
  export let notes;
  export let url = null;

  onMount(async () => {
  });
</script>

{#if url}
<a href={url} target="class-products"><div class="title">{title}</div></a>
{:else}
<div class="title">{title}</div>
{/if}

{#if publisher}
  <div class="publisher">Published : {publisher}</div>
{/if}
<div class="notes">{@html notes}</div>

<style>
a {
  color: blue;
  text-decoration: none;
  font-style: oblique;
}
.title {
  margin-top: 10px;
  font-weight: bold;
}
.notes {
  margin-left: 1.2em;
}
</style>
