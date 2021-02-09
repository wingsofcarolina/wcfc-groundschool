<script>
  import { onMount } from 'svelte'
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

</script>


<div class="section">
  <div class=title>{section}</div>
  <hr class="highlight">

  <div class="handoutlist">
    {#each {length: maxLesson+1} as _, i }
      {#if i != 0}
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
.subtitle {
  font-size: 1.2em;
  font-weight: 550;
  color: rgb(40, 90, 149);
  text-align: center;
  margin-top: 20px;
}
.handoutlist {
  margin-left: 10px;
}
.highlight {
  height: 4px;
  margin-top: 25px;
  margin-bottom: 40px;
  max-width: 250px;
  border-color: rgb(40, 90, 149);
  background-color: rgb(40, 90, 149);
  border-radius: 3px;
  margin: 0 auto;
}
</style>
