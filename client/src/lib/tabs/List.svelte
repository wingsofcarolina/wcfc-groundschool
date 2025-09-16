<script>
  import { onMount } from 'svelte'
  import { notifier } from '@beyonk/svelte-notifications'
  import DeleteAllDialog from "$lib/components/DeleteAllDialog.svelte";

  /** @type {any} */
  let students;
  /** @type {any} */
  let dialog;

  /** @type {any} */
  export let section;

  onMount(async () => {
		getStudents();
	});

  /**
   * @param {any} change
   */
  export function changeClass(change){
    section = change;
    getStudents();
  }

  const getStudents = async () => {
    const response = await fetch('/api/students/' + section.id, {
			method: "get",
      credentials: 'include',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    });
    if (response.ok) {
      students = await response.json();
    } else {
      console.log(response);
      notifier.danger('Retrieve of student failed.');
    }
  }

  /**
   * @param {any} email
   */
  const removeStudent = async (email) => {
    const response = await fetch('/api/student/' + email, {
      method: "delete",
      credentials: 'include',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    });
    if (response.ok) {
      getStudents();
    } else {
      console.log(response);
      notifier.danger('Delete of student failed.');
    }
  }

  const removeAllStudents = async () => {
    dialog.raise();
  }

  /**
   * @param {any} event
   */
  const modify = (event) => {
    getStudents();
  }
</script>

<div class=subtitle>List/Remove Student(s)</div>
<hr>
<p>
  This page lists all the students registered for the selected class. It also
  gives the option of removing a student individually from the class noted
  above. Click on the button next to a student's name/email to remove the student
  from the system.
</p>
<p>
  Additionally, all students from the class selected above can be removed in one
  operation by clicking on the "Remove All Students" button below. Use with care
  and likely after the last class has finished.
</p>
<hr>
<div class="section">
  {#if students && students.length > 0}
    <table>
      <thead>
        <tr><th>Name</th><th>Email</th><th>Remove?</th></tr>
      </thead>
      <tbody>
        {#each students as student}
          <tr>
            <td>{student.name}</td>
            <td>{student.email}</td>
            <td>
              <button type="button" class=remove on:click={() => removeStudent(student.email)} on:keydown={(e) => e.key === 'Enter' || e.key === ' ' ? removeStudent(student.email) : null} aria-label="Delete student {student.name}">
                Delete
              </button>
            </td>
          </tr>
        {/each}
      </tbody>
    </table>
  {:else}
    <span class=nostudents>No students found in this class!</span>
  {/if}
</div>

<hr>
<button on:click={() => removeAllStudents()}>
  Remove All Students From {section.text}
</button>

<DeleteAllDialog bind:this="{dialog}" section={section} on:modify={modify}/>

<style>
p {
  text-align: left;
}
table {
  font-size: 0.75em;
}
.remove {
  color: blue;
  cursor: pointer;
  text-align: center;
  background: none;
  border: none;
  padding: 0;
  font: inherit;
  text-decoration: underline;
}
</style>
