<script>
  import { onMount } from 'svelte'
  import Section from "../components/Section.svelte";

  export let name;
  export let data;

  let maxLesson = 0;

  onMount(async () => {
  });

</script>

<div class="outer">
	<div class="inner">
		{#if data}
			{#each data.children as section}
				{#if section.path == name}
					<Section section={section.label} items={section.children} />
				{/if}
			{/each}
		{/if}
	</div>
</div>

<style>
.outer {
  display: flex;
	flex-direction: column;
	align-items: center;
}
.inner {
	display: flex;
	flex-direction: column;
}
</style>
