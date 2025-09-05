<script>
  import { onMount } from 'svelte'
  import { goto } from '$app/navigation'
  import { user, adminState } from '$lib/store.js'
  import * as notifier from '@beyonk/svelte-notifications/src/notifier.js'
  import tippy from "sveltejs-tippy";
  import Lesson from "$lib/components/Lesson.svelte";
  import UploadDialog from "$lib/components/UploadDialog.svelte";

  /** @type {any} */
  export let section;
  export let label;
  /** @type {any} */
  export let items;

  let maxLesson = 0;
  let uploadOnOff = false;
  /** @type {any} */
  let dialog;

  onMount(async () => {
    for (var i = 0; i < items.length; i++) {
      if (items[i].lesson > maxLesson)
        maxLesson = items[i].lesson;
    }
  });

  function hasEntries(/** @type {any} */ lesson) {
    for (var i = 0; i < items.length; i++) {
      if (items[i].lesson == lesson)
      {
        return true;
      }
    }
    return false;
  }

  function requiredReading() {
    switch (/** @type {any} */ (section)) {
      case "private" :
        goto('private_reading');
        break;
      case "instrument" :
        goto('instrument_reading');
        break;
      case "commercial" :
        goto('commercial_reading');
        break;
      }
  }

  const uploadProps = {
    allowHTML: true,
    placement: "left",
    delay: 800,
    content: "<span class='tooltip'>Upload new file</span>"
  }

</script>

<div class="section">
  <div class=title>{label}</div>
  {#if $user &&  $adminState == 'on' && ! $user.anonymous}
    <div class='upload'>
      <button type="button" class="upload-button" use:tippy={uploadProps} on:click={() => /** @type {any} */ (dialog).raise()} aria-label="Upload new file">
        <img src='upload_icon.png' alt='Upld'>
      </button>
    </div>
  {/if}
  <button type="button" class="internal_button" on:click={() => requiredReading()} on:keydown={(e) => e.key === 'Enter' || e.key === ' ' ? requiredReading() : null}>
    Class Supplies
  </button>
  <hr class="highlight">

  <div class="handoutlist">
    {#each {length: maxLesson+1} as _, lesson }
      {#if hasEntries(lesson) == true}
        <Lesson lesson={lesson} section={section} items={items.filter((/** @type {any} */ items) => items.lesson == lesson)} on:modify/>
      {/if}
    {/each}
  </div>
</div>

<UploadDialog bind:this="{dialog}" section={section} on:modify/>

<style>
.section {
  width: 100%;
  margin-bottom: 3em;
}
.title {
  font-size: 2em;
  text-align: center;
}
.upload {
  float: right;
}
.upload-button {
  background: none;
  border: none;
  padding: 0;
  cursor: pointer;
  display: block;
}
.upload-button img {
  display: block;
}
.internal_button {
  font-size: 1.2em;
  text-align: center;
  cursor: pointer;
  margin-top: 10px;
  margin-bottom: 10px;
  color: blue;
  background: none;
  border: none;
  padding: 0;
  font: inherit;
  width: 100%;
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
