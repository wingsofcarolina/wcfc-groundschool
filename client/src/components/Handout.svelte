<script>
  import { notifier } from '@beyonk/svelte-notifications'

  export let path;
  export let title;

  var data;

  const fetchFile = async (file) => {
    var json = JSON.stringify({
      name: file.path
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

</script>

<div class="handout">
  <span class="title" on:click={() => fetchFile({path})}>{title}</span>
</div>

<style>
.handout {
  margin-top: 10px;
  font-size: 1.1em;
}
.title {
  margin-left: 3em;
  cursor: pointer;
}
</style>
