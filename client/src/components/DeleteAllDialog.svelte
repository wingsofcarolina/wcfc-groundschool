<script>
  import { notifier } from '@beyonk/svelte-notifications'
  import { createEventDispatcher } from 'svelte';

  const dispatch = createEventDispatcher();

  export let visible = false;
  export let section = null;

  let label = null;

  export const raise = () => visible = true

  const cancelDeleteDialog = async () => {
    // var el = document.getElementById("deleteDialog");
    // if (el) el.style.visibility = "hidden";
    visible = false;
  }

  const removeAllStudents = async () => {
    const response = await fetch('/api/students/' + section.id, {
      method: "delete",
      withCredentials: true,
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    });
    if (response.ok) {
      refresh();
    } else {
      console.log(response);
      notifier.danger('Delete of entire class failed.');
    }
    visible = false;
  }

  const refresh = async () => {
    var request = { command : 'refresh' };
    dispatch('modify', request );
  }
</script>


<div id='deleteDialog' class='dialog' style="visibility : {visible ? 'visible' : 'hidden'}">
  <div class='dialog_contents'>
    <div class='dialog_label'>Delete All Students in Class</div>
    <div class='dialog_text'>{section.text}</div>
    <p>
      <button on:click={cancelDeleteDialog.bind()}>No</button>
      <button on:click={removeAllStudents.bind()}>Yes</button>
  </div>
</div>

<style>
button {
  margin: 10px;
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
  position: fixed;
  left: 50%;
  top: 30%;
  transform: translate(-50%, -50%);
  -ms-transform: translate(-50%, -50%);
  -webkit-transform: translate(-50%, -50%);
}
.dialog_label {
  text-align: left;
}
.dialog_text {
  text-align: center;
  margin-top: 10px;
}
</style>
