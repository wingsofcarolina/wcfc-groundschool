<script>
  import { onMount } from 'svelte'
  import { createEventDispatcher } from 'svelte';
  import { user, adminState } from '$lib/store.js'
  import { notifier } from '@beyonk/svelte-notifications'
  import ModifyDialog from '$lib/components/ModifyDialog.svelte'
  import tippy from "sveltejs-tippy";

  export let item;
  export let section;
  export let index = -1;
  export let maxIndex = -1;

  /** @type {any} */
  let dialog;

  const dispatch = createEventDispatcher();

  var data;

  onMount(async () => {
    // console.log('Idx : ' + index + '  Max : ' + maxIndex + ' : ' + item.label);
  });

  /**
   * @param {any} item
   */
  const fetchFile = async (item) => {
    var name = item.item.path;
    var json = JSON.stringify({
      name: name
    });
    const response = await fetch('/api/fetch', {
      method: "post",
      credentials: 'include',
      headers: {
        'Accept': 'application/pdf',
        'Content-Type': 'application/json'
      },
      body: json
    });
    if (!response.ok) {
      notifier.danger('Retrieve of requested document failed.');
    } else {
      data = await response.blob();
      var fileURL = URL.createObjectURL(data);
      window.open(fileURL, 'wcfc-groundschool');
    }
  }

  /**
   * @param {any} item
   */
  const moveUpFile = async (item) => {
    var request = { command : 'moveUp', entity: item };
    dispatch('modify', request );
  }
  /**
   * @param {any} item
   */
  const moveDownFile = async (item) => {
    var request = { command : 'moveDown', entity: item };
    dispatch('modify', request );
  }
  /**
   * @param {any} item
   */
  const deleteFile = async (item) => {
    var request = { command : 'delete', entity: item };
    dispatch('modify', request );
  }
  /**
   * @param {any} item
   */
  const editFileData = async (item) => {
    dialog.raise(item);
  }

  /**
   * @param {...any} list
   */
  function combine(...list){
    return list.reduce(
       (a,b)=>{
         return {...a,...b}
       }
    )
  }

  const globalProps = {
    allowHTML: true,
    placement: "left",
    delay: 800
  }
  const deleteProps = combine ( globalProps, { content: "<span class='tooltip'>Delete file</span>", });
  const editProps = combine ( globalProps, { content: "<span class='tooltip'>Modify file properties</span>", });
  const moveUpProps = combine ( globalProps, { content: "<span class='tooltip'>Move file up</span>", });
  const moveDownProps = combine ( globalProps, { content: "<span class='tooltip'>Move file down</span>", });
  const stateProps = combine ( globalProps, { content: "<span class='tooltip'Show switch state</span>", });
</script>

<div class="handout">
  {#if $user &&  $adminState == 'on' && ! $user.anonymous}
    <button type="button" class="icon-button" use:tippy={deleteProps} on:click={() => deleteFile({item})} aria-label="Delete file">
      <img src='delete_icon.png' alt='Del'>
    </button>
    <button type="button" class="icon-button" use:tippy={editProps} on:click={() => editFileData({item})} aria-label="Edit file properties">
      <img src='edit_icon.png' alt='Edt'>
    </button>
    <img src='blank_icon.png' alt='X'>
    {#if index != 0}
      <button type="button" class="icon-button" use:tippy={moveUpProps} on:click={() => moveUpFile({item})} aria-label="Move file up">
        <img src='arrow_up_icon.png' alt='Up'>
      </button>
    {:else}
      <img src='blank_icon.png' alt='X'>
    {/if}
    {#if index != maxIndex - 1}
      <button type="button" class="icon-button" use:tippy={moveDownProps} on:click={() => moveDownFile({item})} aria-label="Move file down">
        <img src='arrow_down_icon.png' alt='Dwn'>
      </button>
    {:else}
      <img src='blank_icon.png' alt='X'>
    {/if}
  {/if}
  <button type="button" class="title" on:click={() => fetchFile({item})} on:keydown={(e) => e.key === 'Enter' || e.key === ' ' ? fetchFile({item}) : null}>
    {item.label} {item.required ? "" : "(Optional)"}
  </button>
</div>

<ModifyDialog bind:this="{dialog}" section={section} item={item} on:modify/>

<style>
:global(.tooltip) {
  font-size: 0.7rem;
}
.handout {
  margin-top: 10px;
  font-size: 1.1em;
}
.icon-button {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  display: inline-block;
}
.icon-button img {
  display: block;
}
.title {
  display: inline-block;
  margin-left: 3em;
  cursor: pointer;
  width: 35em;
  background: none;
  border: none;
  padding: 0;
  font: inherit;
  text-align: left;
}
</style>
