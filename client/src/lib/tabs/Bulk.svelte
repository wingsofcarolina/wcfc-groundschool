<script>
  import { notifier } from '@beyonk/svelte-notifications'

  /** @type {FileList|null} */
  let files = null;

  /** @type {any} */
  export let section;

  /**
   * @param {any} section
   */
  export function changeClass(section){}

	const uploadCSV = async () => {
    if (files == null) {
      notifier.danger("No file selected.")
    } else {
      const formData = new FormData();
      formData.append('section', section.id);
      formData.append('file', files[0]);

      const response = await fetch('/api/uploadStudents', {
          method: 'post',
          body: formData
      });
      if (response.ok) {
        notifier.success('File uploaded successfully');
      } else {
        console.log(response);
        notifier.danger('File failed to upload');
      }
    }
  }
</script>

<div class=subtitle>Upload CSV</div>
<hr>
<p>
  This page allows the class administrator to upload a list of students in bulk
  using a comma-separated text file. The file needs to conform to a strict format
  to allow the database for the class selected above to be populated correctly.
</p>
<p>
  Select a CSV file using the "Browse.." button then click on "Upload CSV File"
  to deliver the file to the system.
</p>
<div class="section">
  <div class="contact_block">
    <div class="contact_info">
      <input id="file" type="file" bind:files>
      <input id="submit" type="submit" value="Upload CSV File" on:click={() => uploadCSV()}>
    </div>
  </div>
</div>

<style>
p {
  text-align: left;
}
</style>
