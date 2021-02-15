<script>
	import { onMount } from 'svelte'
	import { goto } from '@sapper/app'
	import { notifier } from '@beyonk/svelte-notifications'
	import { user } from '../store.js'
	import Class from "../components/Class.svelte";

	var data = null;

	onMount(async () => {
		getUser();
		getIndex();
	});

	const getUser = async () => {
		const response = await fetch('/api/user', {
			method: "get",
			withCredentials: true,
			headers: {
				'Accept': 'application/json'
			}
		});
		if (!response.ok) {
			if (response.status == 404) {
				// User was simpoly not found, therefore not authenticated
				goto('login');
			} else {
				// Otherwise, something else went wrong
				notifier.danger('Retrieve of user information failed.');
			}
		} else {
			var tmp = await response.json();
			user.set(tmp);
		}
	}

	const getIndex = async () => {
		const response = await fetch('/api/index', {
			method: "get",
			withCredentials: true,
			headers: {
				'Accept': 'application/json'
			}
		});
		if (!response.ok) {
			if (response.status == 401) {
				goto('login');
			} else {
				notifier.danger('Retrieve of class index failed.');
			}
		} else {
			data = await response.json();
		}
	}

</script>

<svelte:head>
	<title>WCFC Instrument Materials</title>
</svelte:head>

	<Class name="/Instrument Groundschool" data={data} />

<style>
@media (min-width: 480px) {
}
</style>
