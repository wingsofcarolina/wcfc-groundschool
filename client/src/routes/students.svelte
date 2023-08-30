<script>
	import { onMount } from 'svelte';
	import { goto } from '@sapper/app';
	import { notifier } from '@beyonk/svelte-notifications'
	import { user } from '../store.js'
	import { getUser } from '../common.js'
	import Tabs from "../tabs/Tabs.svelte";
	import Single from "../tabs/Single.svelte"
	import Bulk from "../tabs/Bulk.svelte"
	import List from "../tabs/List.svelte"
	import Email from "../tabs/Email.svelte"

	let view = false;
	let name = null;
	let email = null;
	let files = null;

	let tabComponent;

  // List of tab items with labels, values and assigned components
  let items = [
    { label: "Single", value: 1, component: Single },
    { label: "Bulk", value: 2, component: Bulk },
		{ label: "List", value: 3, component: List },
		//{ label: "Email", value: 4, component: Email }
  ];

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
</script>

<svelte:head>
	<title>Students</title>
</svelte:head>

{#if view}
	<div class=title>Students</div>
	<hr class="highlight">

	<center>
		<div class="narrow">
			Groundschool Class<br>
			<select class="class_selection"
						bind:value={section}
						on:change={() => tabComponent.changeClass(section)}>
				{#each sections as s}
					<option value={s}>
						{s.text}
					</option>
				{/each}
			</select>

			<Tabs section={section} bind:this={tabComponent} {items} />

		</div>
	</center>
{/if}
<style>
file {
	width: 400px;
}
.class_selection {
	width: 200px;
	text-align: center;
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
