<script>
	import { onMount } from 'svelte';
	import { goto } from '@sapper/app';
	import { notifier } from '@beyonk/svelte-notifications'
	import { user } from '../store.js'
	import { getUser } from '../common.js'

	let view = false;
	let name = null;
	let email = null;
	let files = null;

	let sections = [
		{ id: 'PRIVATE', text: `Private` },
		{ id: 'INSTRUMENT', text: `Instrument` },
		{ id: 'COMMERCIAL', text: `Commercial` }
	];
	let section = sections[0];

	onMount(function() {
		getUser();
		if ($user == null || $user.admin != true ) {
			notifier.danger('Access not authorized.');
			goto('/');
		} else {
			view = true;
		}
	});

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
        section = null;
      } else {
        console.log(response);
        notifier.danger('Student add failed, what gives?');
      }
    }
  }

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
        section = null;
      } else {
        console.log(response);
        notifier.danger('File failed to upload');
      }
    }
  }

</script>

<svelte:head>
	<title>Login</title>
</svelte:head>

{#if view}
	<div class=title>Students</div>
	<hr class="highlight">

	<center>
		<div class="narrow">

			<p> This is a student management page which allows class administrators to
				upload a CSV file with all students, or enter students one at a time. </p>

				<select class="class_selection" bind:value={section}>
					{#each sections as s}
						<option value={s}>
							{s.text}
						</option>
					{/each}
				</select>
				:: Select Class

			<div class=subtitle>Add Student</div>
			<hr>
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
						<input id="submit" type="submit" value="Submit Login" on:click={() => addStudent()}>
					</div>
				</div>
			</div>

			<div class=subtitle>Upload CSV</div>
			<hr>
			<div class="section">
				<div class="contact_block">
					<div class="contact_info">
						<input id="file" type="file" bind:files>
						<input id="submit" type="submit" value="Upload CSV File" on:click={() => uploadCSV()}>
					</div>
				</div>
			</div>

		</div>
	</center>
{/if}

<style>
file {
	width: 400px;
}
.class_selection {
	width: 200px;
}
.auth {
	margin-top: 3em;
}
.title {
  font-size: 2em;
  text-align: center;
}
.subtitle {
  font-size: 1.5em;
  text-align: center;
}
hr {
	width: 250px;
	height: 2px;
}
.highlight {
  height: 4px;
  margin-top: 25px;
  margin-bottom: 40px;
  width: 250px;
  border-color: rgb(40, 90, 149);
  background-color: rgb(40, 90, 149);
  border-radius: 3px;
	margin: 0px auto 50px auto;
}
.section {
  width: 100%;
  margin-bottom: 3em;
}
.narrow {
	width: 70%;
}
.narrow p {
	text-align: left;
}
.contact_block {
  display: flex;
  justify-content: space-around;
  margin: 20px;
  font-size: 20px;
}
.contact_info {
  text-align: center;
}
input, textarea {
	font-family: 'Courier', sans-serif;
	padding: 10px;
	margin: 10px 0;
	border:0;
	box-shadow:0 0 15px 4px rgba(0,0,0,0.06);
	font-family: inherit;
}
input[type=submit] {
		padding:5px 15px;
		background:#ccc;
		border:0 none;
		cursor:pointer;
		-webkit-border-radius: 5px;
		border-radius: 5px;
		height: 50px;
		width: 100%;
}
</style>
