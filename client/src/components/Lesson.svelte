<script>
  import { onMount } from 'svelte'
  import { goto } from '@sapper/app'
  import { user } from '../store.js'
  import Handout from "../components/Handout.svelte";

  export let lesson;
  export let items;
  export let section;

  let handouts = items.filter(items => items.lesson == lesson);

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
.section {
  width: 100%;
  margin-bottom: 3em;
}
.title {
  font-size: 2em;
  text-align: center;
}
.internal_button {
  font-size: 1.2em;
  text-align: center;
  cursor: pointer;
  margin-top: 10px;
  margin-bottom: 10px;
  color: blue;
}
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
.highlight {
  height: 4px;
  max-width: 250px;
  border-color: rgb(40, 90, 149);
  background-color: rgb(40, 90, 149);
  border-radius: 3px;
  margin: 0px auto 50px auto;
}
</style>
