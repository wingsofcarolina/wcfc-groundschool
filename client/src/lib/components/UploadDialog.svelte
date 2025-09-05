<script>
  import * as notifier from '@beyonk/svelte-notifications/src/notifier.js'
  import { createEventDispatcher } from 'svelte';

  const dispatch = createEventDispatcher();

  export let visible = false;
  export let section = 0;

  /** @type {any} */
  let label = null;
  let required = true;
  /** @type {any} */
  let lesson = null;
  /** @type {any} */
  let files = null;

  export const raise = () => visible = true

  const cancelUploadDialog = async () => {
    // var el = document.getElementById("uploadDialog");
    // if (el) el.style.visibility = "hidden";
    visible = false;
  }

  /**
   * @param {any} item
   */
  const refresh = async (item) => {
    var request = { command : 'refresh' };
    dispatch('modify', request );
  }

  const uploadNewHandout = async () => {
    visible = false;

    if (files == null || label == null || lesson == null) {
      notifier.danger("All values must be provided.")
    } else {
      const formData = new FormData();
      formData.append('label', label);
      formData.append('required', required.toString());
      formData.append('section', section.toString());
      formData.append('lesson', lesson);
      formData.append('file', files[0]);
      // for (var pair of formData.entries()) {
      //     console.log(pair[0]+ ', ' + pair[1]);
      // }
      const response = await fetch('/api/upload', {
          method: 'post',
          body: formData
      });
      if (response.ok) {
        notifier.success('File uploaded successfully');
        lesson = label = null;
        refresh(null);
      } else {
        console.log(response);
        if (response.status == 413)
          notifier.danger('File too large!');
        else
          notifier.danger('File failed to upload (not a PDF??)');
      }
    }
  }
</script>


<div id='uploadDialog' class='dialog' style="visibility : {visible ? 'visible' : 'hidden'}">
  <div class='dialog_contents'>
    <div class='dialog_label'>Handout Display Name</div>
    <input bind:value={label}><br>
    <div class='dialog_label'>Class Number</div>
    <input width=10 bind:value={lesson}><br>
    <input id="file" type="file" bind:files><br>
    <div class='radio_button'>
      <label>
        <input type="radio" bind:group={required} name="required" value={true} />
        Required
      </label>
      <label>
        <input type="radio" bind:group={required} name="required" value={false} />
        Optional
      </label>
    </div>
    <p>
    <button on:click={cancelUploadDialog}>Cancel</button>
    <input type="submit" value="Submit" on:click={uploadNewHandout}>
  </div>
</div>

<style>
input {
  margin: 10px;
}
.radio_button {
  text-align: left;
}
.dialog {
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
}
.dialog_contents {
  margin: 100px auto;
  background-color: #f2f2f2;
  border-radius: 10px;
  -webkit-border-radius: 10px;
  -moz-border-radius:  10px;
  border:1px solid #666666;
  padding:15px;
  text-align:center;
  font-weight: bold;
  font-size: 15px;
  border: 3px solid #cccccc;
  position: absolute;
  left: 50%;
  top: 100px;
  transform: translate(-50%, -50%);
  -ms-transform: translate(-50%, -50%);
  -webkit-transform: translate(-50%, -50%);
}
.dialog_label {
  text-align: left;
}
</style>
