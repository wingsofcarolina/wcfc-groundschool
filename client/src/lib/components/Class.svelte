<script>
  import { onMount } from 'svelte'
  import { goto } from '$app/navigation';
  import * as notifier from '@beyonk/svelte-notifications/src/notifier.js'
  import Section from "$lib/components/Section.svelte";

  export let section;

  /** @type {any} */
  let data = null;
  let maxLesson = 0;

  onMount(async () => {
    getIndex(section);
  });

  /**
   * @param {any} section
   */
  const getIndex = async (section) => {
    const response = await fetch('/api/index/' + section, {
      method: "get",
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
      }
    });
    if (!response.ok) {
      if (response.status == 401) {
        console.log('User not authenticated, requesting authentication');
        goto('login');
      } else {
        notifier.danger('Retrieve of class index failed.');
      }
    } else {
      data = await response.json();
    }
  }

  /**
   * @param {number} milliseconds
   */
  const sleep = (milliseconds) => {
    return new Promise(resolve => setTimeout(resolve, milliseconds))
  }

  /**
   * @param {any} item
   */
  const deleteItem = async (item) => {
    const response = await fetch( '/api/delete/' + section + '?path=' + item.path, {
      method: "delete",
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
      }
    });
    if (!response.ok) {
      if (response.status == 401) {
        goto('login');
      } else {
        notifier.danger('Delete of file failed.');
      }
    } else {
      notifier.success("'" + item.label + "' deleted.");
    }
  }

  /**
   * @param {any} direction
   * @param {any} item
   */
  const move = async (direction, item) => {
    const response = await fetch( '/api/' + direction + '/' + section + '?path=' + item.path, {
      method: "put",
      credentials: 'include',
      headers: {
        'Accept': 'application/json'
      }
    });
    if (!response.ok) {
      if (response.status == 401) {
        goto('login');
      } else {
        notifier.danger('Move up of file failed.');
      }
    }
  }

  /**
   * @param {any} event
   */
  const modify = async (event) => {
    var entity = event.detail.entity;
    if (entity != null) {
      var item = entity.item;
    }
    var command = event.detail.command;

    if (command == 'delete') {
      await deleteItem(item);
    } else if (command == 'moveUp') {
      await move('moveUp', item);
    } else if (command == 'moveDown') {
      await move('moveDown', item);
    }
    await sleep(200);
    getIndex(section);
  }
</script>

<div class="outer">
	<div class="inner">
		{#if /** @type {any} */ (data)}
      {#key data}
			   <Section section={section} label={data.label} items={data.children} on:modify={modify}/>
      {/key}
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
