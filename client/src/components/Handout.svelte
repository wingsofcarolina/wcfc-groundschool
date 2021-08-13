<script>
  import { onMount } from 'svelte'
  import { createEventDispatcher } from 'svelte';
  import { user, adminState } from '../store.js'
  import { notifier } from '@beyonk/svelte-notifications'
  import ModifyDialog from '../components/ModifyDialog.svelte'
  import tippy from "sveltejs-tippy";

  export let item;
  export let section;
  export let index = -1;
  export let maxIndex = -1;

  let dialog;

  const dispatch = createEventDispatcher();

  var data;

  onMount(async () => {
    // console.log('Idx : ' + index + '  Max : ' + maxIndex + ' : ' + item.label);
  });

  const fetchFile = async (item) => {
    var name = item.item.path;
    var json = JSON.stringify({
      name: name
    });
    const response = await fetch('/api/fetch', {
      method: "post",
      withCredentials: true,
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

  const moveUpFile = async (item) => {
    var request = { command : 'moveUp', entity: item };
    dispatch('modify', request );
  }
  const moveDownFile = async (item) => {
    var request = { command : 'moveDown', entity: item };
    dispatch('modify', request );
  }
  const deleteFile = async (item) => {
    var request = { command : 'delete', entity: item };
    dispatch('modify', request );
  }
  const editFileData = async (item) => {
    dialog.raise(item);
  }

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
    <img style="cursor:pointer;" use:tippy={deleteProps} src='delete_icon.png' alt='Del' on:click={() => deleteFile({item})}>
    <img style="cursor:pointer;" use:tippy={editProps} src='edit_icon.png' alt='Edt' on:click={() => editFileData({item})}>
    <img src='blank_icon.png' alt='X'>
    {#if index != 0}
      <img style="cursor:pointer;" use:tippy={moveUpProps} src='arrow_up_icon.png' alt='Up' on:click={() => moveUpFile({item})}>
    {:else}
      <img src='blank_icon.png' alt='X'>
    {/if}
    {#if index != maxIndex - 1}
      <img style="cursor:pointer;" use:tippy={moveDownProps} src='arrow_down_icon.png' alt='Dwn' on:click={() => moveDownFile({item})}>
    {:else}
      <img src='blank_icon.png' alt='X'>
    {/if}
  {/if}
  <span class="title" on:click={() => fetchFile({item})}>{item.label}</span>
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
.title {
  margin-left: 3em;
  cursor: pointer;
}
</style>
