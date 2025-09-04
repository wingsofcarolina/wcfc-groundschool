<script>
  import { onMount } from 'svelte'
  import { goto } from '$app/navigation'
  import { user } from '$lib/store.js'
  import Handout from "$lib/components/Handout.svelte";

  export let lesson;
  export let items;
  export let section;

  let handouts = items.filter(items => items.lesson == lesson);

  console.log('Handouts : ', handouts);
  onMount(async () => {
  });

</script>

<div class="handoutlist">
  {#if lesson != 0}
    <div class="subtitle">Class # {lesson}</div>
  {/if}

  {#if handouts}
    {#each handouts as item, index}
      <Handout section={section} item={item} index={index} maxIndex={handouts.length} on:modify/>
    {/each}
  {/if}
</div>

<style>
.subtitle {
  font-size: 1.2em;
  font-weight: 550;
  color: rgb(40, 90, 149);
  text-align: left;
  margin-top: 20px;
  margin-left: 2.9em;
  border-bottom: 2px solid rgb(40, 90, 149);
}
.handoutlist {
  margin-left: 10px;
}
</style>
