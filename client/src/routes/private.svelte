<script>
	import { onMount } from 'svelte'
	import { goto } from '@sapper/app'
	import { user } from '../store.js'
	import { getUser } from '../common.js'
	import Class from "../components/Class.svelte";

	onMount(async () => {
		getUser();
	});

</script>

<svelte:head>
	<title>WCFC Private Materials</title>
</svelte:head>

	<Class section='private' />

<style>
@media (min-width: 480px) {
}
</style>
