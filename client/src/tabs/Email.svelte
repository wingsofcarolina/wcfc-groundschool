<script>
  import { notifier } from '@beyonk/svelte-notifications'

  let text = null;

  export let section;

  export function changeClass(section){}

  function emailAllStudents() {}

</script>

<div class=subtitle>Email All Students</div>
<hr>
<p>
  This page allows the class administrators to email all students, in bulk,
  in the class indicated above. There is no feedback regarding whether all the
  emails are delivered successfully, and (currently) no mechanism with which to
  add attachments.
</p>
<hr>
<center>
  <div class="section">
    <div class=email>
      <textarea placeholder="Email to students ......" bind:value={text} />
    </div>

    <button on:click={() => emailAllStudents()}>
      Email All Students In {section.text}
    </button>
  </div>
</center>

<style>
p {
  text-align: left;
}
textarea {
  width: 100%;
  height: 200px;
}
</style>
