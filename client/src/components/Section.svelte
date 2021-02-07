<script>
  import { onMount } from 'svelte'
  import Handout from "../components/Handout.svelte";

  export let section;
  export let items;

  onMount(async () => {
    console.log('Items : ', items);
  });

</script>


<div class="section">
  <div class=title>{section}</div>
  <hr class="highlight">

  <div class="handoutlist">
    {#each items as item}
      <Handout path={item.path} title={item.label} />
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
.handoutlist {
  margin-left: 10px;
}
.highlight {
  height: 4px;
  margin-top: 25px;
  margin-bottom: 40px;
  max-width: 250px;
  border-color: rgba(40, 90, 149, 1);
  background-color: rgba(40, 90, 149, 1);
  border-radius: 3px;
  margin: 0 auto;
}
</style>
