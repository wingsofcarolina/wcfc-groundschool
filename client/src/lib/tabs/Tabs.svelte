<script>
  /** @type {any[]} */
  export let items = [];
  export let activeTabValue = 1;

  /** @type {any} */
  export let section;
  /** @type {any} */
  let tabComponent;

  /**
   * @param {any} tabValue
   */
  const handleClick = tabValue => () => (activeTabValue = tabValue);

  /**
   * @param {any} section
   */
  export function changeClass(section){
    console.log('Tabs : changing class')
    tabComponent.changeClass(section);
  }
</script>

<ul>
{#each items as item}
	<li class={activeTabValue === item.value ? 'active' : ''}>
		<button type="button" on:click={handleClick(item.value)} on:keydown={(e) => e.key === 'Enter' || e.key === ' ' ? handleClick(item.value)() : null} aria-label="Switch to {item.label} tab">
			{item.label}
		</button>
	</li>
{/each}
</ul>
{#each items as item}
	{#if activeTabValue == item.value}
  	<div class="box">
  		<svelte:component this={item.component} bind:this={tabComponent} section={section}/>
  	</div>
	{/if}
{/each}
<style>
	.box {
		margin-bottom: 10px;
		padding: 40px;
		border: 1px solid #dee2e6;
    border-radius: 0 0 .5rem .5rem;
    border-top: 0;
	}
  ul {
    display: flex;
    flex-wrap: wrap;
    padding-left: 0;
    margin-bottom: 0;
    list-style: none;
    border-bottom: 1px solid #dee2e6;
  }
	li {
		margin-bottom: -1px;
	}

  button {
    border: 1px solid transparent;
    border-top-left-radius: 0.25rem;
    border-top-right-radius: 0.25rem;
    display: block;
    padding: 0.5rem 1rem;
    cursor: pointer;
    background: none;
    font: inherit;
    width: 100%;
    text-align: left;
  }

  button:hover {
    border-color: #e9ecef #e9ecef #dee2e6;
  }

  li.active > button {
    color: #495057;
    background-color: #d3dee6;
    border-color: #dee2e6 #dee2e6 #fff;
  }
</style>
