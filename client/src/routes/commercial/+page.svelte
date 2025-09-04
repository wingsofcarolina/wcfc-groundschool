<script>
	import { onMount } from 'svelte'
	import { goto } from '$app/navigation'
	import { user } from '$lib/store.js'
	import { getUser } from '$lib/common.js'
	import Class from "$lib/components/Class.svelte";


	onMount(async () => {
		getUser();
	});

</script>

<svelte:head>
	<title>WCFC Commercial Materials</title>
</svelte:head>

	<Class section='commercial' />

<style>
@media (min-width: 480px) {
}
</style>
