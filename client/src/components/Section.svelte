<script>
  import { onMount } from 'svelte'
  import { goto } from '@sapper/app'
  import Handout from "../components/Handout.svelte";

  export let section;
  export let items;

  let maxLesson = 0;

  onMount(async () => {
    for (var i = 0; i < items.length; i++) {
      if (items[i].lesson > maxLesson)
        maxLesson = items[i].lesson;
    }
  });

  function hasEntries(lesson) {
    for (var i = 0; i < items.length; i++) {
      if (items[i].lesson == lesson)
      {
        return true;
      }
    }
    return false;
  }

  function requiredReading() {
    switch (section) {
      case "Private Class Materials" :
        goto('private_reading');
        break;
      case "Instrument Class Materials" :
        goto('instrument_reading');
        break;
      case "Commercial Class Materials" :
        goto('commercial_reading');
        break;
      }
  }
</script>


<div class="section">
  <div class=title>{section}</div>
  <div class="internal_button" on:click={() => requiredReading()}>Class Supplies</div>
  <hr class="highlight">

  <div class="handoutlist">
    {#each {length: maxLesson+1} as _, i }
      {#if i != 0  && hasEntries(i) == true}
        <div class="subtitle">Class # {i}</div>
      {/if}
      {#each items as item}
        {#if item.lesson == i}
          <Handout path={item.path} title={item.label} />
        {/if}
      {/each}
    {/each}
  </div>
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
