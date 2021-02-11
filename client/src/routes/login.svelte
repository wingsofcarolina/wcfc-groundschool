<script>
	import { onMount } from 'svelte';
	import { goto } from '@sapper/app';

	onMount(function() {
	});
</script>

<svelte:head>
	<title>Login</title>
</svelte:head>

<h1>Login</h1>

<p> This page is simply being used to work through the issues associated with
	the possibility of using Slack as an authentication vehicle. We'll see how
	that works out.
</p>
<center>
	<a href="https://slack.com/oauth/v2/authorize?user_scope=identity.basic,identity.email&client_id=REDACTED">
		<img alt="Sign in with Slack" height="40" width="172" src="https://platform.slack-edge.com/img/sign_in_with_slack.png"
		 srcset="https://platform.slack-edge.com/img/sign_in_with_slack.png 1x, https://platform.slack-edge.com/img/sign_in_with_slack@2x.png 2x" />
	</a>
</center>
