<script>
  import { onMount } from 'svelte'
  import { goto } from '@sapper/app';
  import { notifier } from '@beyonk/svelte-notifications'
  import Section from "../components/Section.svelte";

  export let section;

  let data = null;
  let maxLesson = 0;

  onMount(async () => {
    getIndex(section);
  });

	const getIndex = async (section) => {
		const response = await fetch('/api/index/' + section, {
			method: "get",
			withCredentials: true,
			headers: {
				'Accept': 'application/json'
			}
		});
		if (!response.ok) {
			if (response.status == 401) {
        console.log('User not authenticated, redirecting to Slack');
				goto('login');
			} else {
				notifier.danger('Retrieve of class index failed.');
			}
		} else {
			data = await response.json();
		}
	}

	const sleep = (milliseconds) => {
	  return new Promise(resolve => setTimeout(resolve, milliseconds))
	}

	const deleteItem = async (item) => {
		const response = await fetch( '/api/delete/' + section + '?path=' + item.path, {
			method: "delete",
			withCredentials: true,
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

	const move = async (direction, item) => {
		const response = await fetch( '/api/' + direction + '/' + section + '?path=' + item.path, {
			method: "put",
			withCredentials: true,
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

  const modify = (event) => {
    var entity = event.detail.entity;
    if (entity != null) {
		   var item = entity.item;
    }
		var command = event.detail.command;

		if (command == 'delete') {
			deleteItem(item);
		} else if (command == 'moveUp') {
			move('moveUp', item);
		} else if (command == 'moveDown') {
			move('moveDown', item);
		}
		sleep(200).then(() => {
			getIndex(section);
		})
  }
</script>

<div class="outer">
	<div class="inner">
		{#if data}
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
