<script>
  import { notifier } from '@beyonk/svelte-notifications'

  let name = null;
  let email = null;

  export let section;

  export function changeClass(section){}

  const addStudent = async () => {
    if (name == null || email == null) {
      notifier.danger("Either name or email missing, both required.")
    } else {
			var json = JSON.stringify({
				section: section.id,
				name: name,
				email: email
			});

      const response = await fetch('/api/addStudent', {
				method: "post",
        withCredentials: true,
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json'
        },
        body: json
      });
      if (response.ok) {
        notifier.success('Student added successfully');
        name = email = null;
      } else {
        console.log(response);
        if (response.status === 409) {
          let json = await response.json()
          notifier.danger(json.message)
        }
        notifier.danger('Student add failed, what gives?');
      }
    }
  }
</script>

<div class=subtitle>Add Student</div>
<hr>
<p>
  This page allows the class administrator to add an individual student. Enter
  the student's full name and email address and click on "Submit Login" to add
  the student to the database for the class noted above.
</p>
<div class="section">
  <div class="contact_block">
    <div class="contact_info">
      <div class="contact_row">
        <input type="text" id="name" name="name" placeholder="Student Name"
        size=40 bind:value={name}>
      </div>
      <div class="contact_row">
        <input type="text" id="email" name="email" placeholder="Email Address"
        size=40 bind:value={email}>
      </div>
      <input id="submit" type="submit" value="Submit Student" on:click={() => addStudent()}>
    </div>
  </div>
</div>

<style>
p {
    text-align: left;
}
</style>
